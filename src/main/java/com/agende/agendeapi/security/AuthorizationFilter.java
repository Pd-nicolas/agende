package com.agende.agendeapi.security;

import com.agende.agendeapi.hibernate.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AuthorizationFilter extends BasicAuthenticationFilter {


    public AuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        if (isPreflight(req)) {
            res.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        String header = req.getHeader(SecurityUtil.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityUtil.TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
        } finally {
            TenantContext.clear();
        }
    }


    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityUtil.HEADER_STRING);

        if (token != null) {
            Claims claims = SecurityUtil.extractAuthorities(token);

            String user = claims.getSubject();
            String schema = (String) claims.get("schema");

            if (schema != null) {
                TenantContext.setCurrentTenant(schema);
            }

            ArrayList<LinkedHashMap> map = (ArrayList) claims.get("authorities");

            ArrayList<GrantedAuthority> lista = new ArrayList<>();

            for (LinkedHashMap key: map) {
                lista.add(new SimpleGrantedAuthority(key.values().iterator().next().toString()));
            }

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, lista);
            }

            return null;
        }

        return null;
    }

    private boolean isPreflight(HttpServletRequest request) {
        return "OPTIONS".equals(request.getMethod());
    }
}
