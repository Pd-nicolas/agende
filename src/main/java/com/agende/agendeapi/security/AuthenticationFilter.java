package com.agende.agendeapi.security;

import com.agende.agendeapi.config.SpringApplicationContext;
import com.agende.agendeapi.controller.dto.request.UsuarioLoginRequestDTO;
import com.agende.agendeapi.controller.dto.response.PerfilResponseDTO;
import com.agende.agendeapi.controller.dto.response.UsuarioLoginResponseDTO;
import com.agende.agendeapi.entity.Usuario;
import com.agende.agendeapi.hibernate.TenantContext;
import com.agende.agendeapi.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest req, final HttpServletResponse res)
            throws AuthenticationException {
        try {

            if (isPreflight(req)) {
                res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {

                UsuarioLoginRequestDTO creds = new ObjectMapper().readValue(req.getInputStream(), UsuarioLoginRequestDTO.class);

                TenantContext.setCurrentTenant("public");

                logger.info("Authentication with email: {}", creds.getEmail());

                return authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getSenha(), new ArrayList<>())
                );

            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String userName = ((User) auth.getPrincipal()).getUsername();

        UsuarioService userService = (UsuarioService) SpringApplicationContext.getBean("usuarioService");
        Usuario usuario = userService.findByEmail(userName);

        String schemaAtual = TenantContext.getCurrentTenant();

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", auth.getAuthorities());
        claims.put("schema", schemaAtual);

        //TODO remover client do response
        claims.put("clienteId", null);
        claims.put("clienteCodigo", null);

        String token = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityUtil.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityUtil.getTokenSecret())
                .addClaims(claims)
                .compact();

        UsuarioLoginResponseDTO success = UsuarioLoginResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .ativo(usuario.getAtivo())
                .perfil(auth.getAuthorities().stream()
                        .map(a -> new PerfilResponseDTO(null, a.getAuthority()))
                        .collect(Collectors.toList()))
                .token(token)
                .schema(schemaAtual)
                .primeiroAcesso(usuario.getPrimeiroAcesso())
                .build();

        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        new ObjectMapper().writeValue(res.getWriter(), success);
        res.addHeader("Authorization", "Bearer " + token);
    }

    private boolean isPreflight(HttpServletRequest request) {
        return "OPTIONS".equals(request.getMethod());
    }

}
