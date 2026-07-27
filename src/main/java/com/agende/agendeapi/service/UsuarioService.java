package com.agende.agendeapi.service;

import com.agende.agendeapi.controller.dto.request.AlterarSenhaRequestDTO;
import com.agende.agendeapi.entity.Cliente;
import com.agende.agendeapi.entity.Perfil;
import com.agende.agendeapi.entity.Usuario;
import com.agende.agendeapi.hibernate.TenantContext;
import com.agende.agendeapi.repository.UsuarioRepository;
import com.agende.agendeapi.service.interfaces.IUsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements IUsuarioService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final PerfilService perfilService;
    private final ClienteService clienteService;

    public UsuarioService(UsuarioRepository repository,
                          PasswordEncoder passwordEncoder,
                          PerfilService perfilService,
                          @Lazy ClienteService clienteService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.perfilService = perfilService;
        this.clienteService = clienteService;
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario findByCpf(String cpf) {
        logger.debug("Buscando usuário por CPF: {}", cpf);
        return repository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    @Override
    public Usuario criarUsuario(Usuario usuario) {
        logger.info("Iniciando criação de usuário para CPF: {}", usuario.getCpf());

        validarEmailECpfUnicos(usuario.getEmail(), usuario.getCpf(), null);

        Usuario logado = getUsuarioLogado();

        boolean isAdmin = logado.getPerfis().stream()
                .anyMatch(p -> "ADMIN".equals(p.getDescricao()));
        // Registrar usuario no tenant do admin
        if (isAdmin) {
            String schema = repository.findSchemaByEmail(logado.getEmail());

            Cliente cliente = clienteService.buscarPorSchema(schema);

            usuario.setClientes(List.of(cliente));
        }

        if (usuario.getClientes() == null || usuario.getClientes().isEmpty()) {
            throw new RuntimeException("Usuário deve pertencer a 1 cliente.");
        }

        Long clienteId = usuario.getClientes().get(0).getId();
        Cliente cliente = clienteService.buscarPorId(clienteId);

        if (cliente == null) {
            throw new RuntimeException("Cliente informado não existe.");
        }

        String schemaCliente = cliente.getSchema();
        if (schemaCliente == null || schemaCliente.isBlank()) {
            throw new RuntimeException("Cliente não possui schema configurado.");
        }

        usuario.setClientes(List.of(cliente));

        List<Perfil> perfisValidos = usuario.getPerfis().stream()
                .map(perfil -> perfilService.findById(perfil.getId()))
                .collect(Collectors.toList());

        usuario.setPerfis(perfisValidos);

        String senhaGerada = gerarSenhaAleatoria(10);
        usuario.setSenha(passwordEncoder.encode(senhaGerada));
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());

        String tenantOriginal = TenantContext.getCurrentTenant();
        TenantContext.setCurrentTenant(schemaCliente);

        logger.info("Salvando usuário no schema do cliente: {}", schemaCliente);

        Usuario usuarioCriado = repository.save(usuario);

        TenantContext.setCurrentTenant("public");
        repository.registrarUsuarioNoPublic(
                usuario.getEmail(),
                cliente.getId(),
                schemaCliente
        );

        TenantContext.setCurrentTenant(tenantOriginal);

        return usuarioCriado;
    }

    @Transactional
    @Override
    public Usuario criarPrimeiroUsuario(Usuario usuario) {

        if (existsByPerfis_Id(1L)) {
            throw new RuntimeException("Já existe um usuário master no sistema.");
        }

        TenantContext.setCurrentTenant("public");

        Perfil perfilMaster = perfilService.findById(1L);

        usuario.setPerfis(List.of(perfilMaster));
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());

        usuario.setClientes(null);
        String senhaGerada = gerarSenhaAleatoria(10);
        usuario.setSenha(passwordEncoder.encode(senhaGerada));

        Usuario usuarioCriado = repository.save(usuario);

        return usuarioCriado;
    }

    @Override
    public Page<Usuario> listarUsuariosPaginados(Integer paginaAtual, Integer tamanhoPagina, String direcao,
                                                 String ordenacao, String filtro) {
        Sort sort = Sort.by(Sort.Direction.fromString(direcao), ordenacao);
        Pageable pageable = PageRequest.of(paginaAtual, tamanhoPagina, sort);
        return repository.findUsuariosFiltrados(filtro, pageable);
    }

    @Transactional
    @Override
    public Usuario editarUsuario(Usuario usuario, Long id) {
        Usuario usuarioExistente = repository.findByIdAndAtivoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado ou não está ativo"));

        validarEmailECpfUnicos(usuario.getEmail(), usuario.getCpf(), usuarioExistente.getId());

        usuarioExistente.setNome(usuario.getNome());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.getPerfis().clear();
        usuarioExistente.setCpf(usuario.getCpf());

        if (usuario.getPerfis() != null && !usuario.getPerfis().isEmpty()) {
            for (Perfil perfil : usuario.getPerfis()) {
                Perfil perfilExistente = perfilService.findById(perfil.getId());
                usuarioExistente.getPerfis().add(perfilExistente);
            }
        }

        return repository.save(usuarioExistente);
    }

    @Transactional
    @Override
    public Usuario excluirUsuario(Long id) {
        Usuario usuarioExistente = repository.findByIdAndAtivoIsTrue(id)
                .orElseThrow(() -> new RuntimeException("Usuário não ativo"));

        if (usuarioExistente.getPerfis().stream().anyMatch(p -> "ADMIN".equals(p.getDescricao()))) {
            throw new RuntimeException("Somente o admin master pode excluir um usuário admin");
        }
        usuarioExistente.setAtivo(false);
        return repository.save(usuarioExistente);
    }

    @Transactional
    @Override
    public Usuario ativarUsuario(Long id) {
        Usuario usuarioExistente = repository.findByIdAndAtivoIsFalse(id)
                .orElseThrow(() -> new RuntimeException("Usuário já está ativo"));
        usuarioExistente.setAtivo(false);
        return repository.save(usuarioExistente);
    }

    @Override
    public boolean existsByPerfis_Id(Long idperfil) {
        TenantContext.setCurrentTenant("public");
        return repository.existsByPerfis_Id(idperfil);
    }

    private String gerarSenhaAleatoria(Integer tamanho) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return sb.toString();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        TenantContext.setCurrentTenant("public");

        String schema = repository.findSchemaByEmail(email);

        if (schema == null) {

            Usuario master = repository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário inexistente"));

            boolean isMaster = master.getPerfis().stream()
                    .anyMatch(p -> p.getId() == 1L);

            if (!isMaster) {
                throw new UsernameNotFoundException("Usuário não possui cliente registrado.");
            }

            return new User(
                    master.getEmail(),
                    master.getSenha(),
                    master.getAtivo(),
                    true,
                    true,
                    true,
                    master.getPerfis().stream()
                            .map(p -> new SimpleGrantedAuthority(p.getDescricao()))
                            .toList()
            );
        }

        Cliente cliente = clienteService.buscarPorSchema(schema);

        if (!cliente.getAtivo()) {
            throw new RuntimeException("Este ambiente está desativado. Contate o administrador.");
        }

        TenantContext.setCurrentTenant(schema);

        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado no schema do cliente: " + schema)
                );

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getAtivo(),
                true,
                true,
                true,
                usuario.getPerfis().stream()
                        .map(p -> new SimpleGrantedAuthority(p.getDescricao()))
                        .toList()
        );
    }

    @Override
    public Usuario findByEmail(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.getPerfis().size();
        return usuario;
    }

    private void validarEmailECpfUnicos(String email, String cpf, Long idAtual) {
        repository.findByEmail(email).ifPresent(usuario -> {
            if (!usuario.getId().equals(idAtual)) {
                throw new RuntimeException("E-mail já utilizado por outro usuário.");
            }
        });

        if (cpf != null && !cpf.trim().isEmpty()) {
            repository.findByCpf(cpf).ifPresent(usuario -> {
                if (!usuario.getId().equals(idAtual)) {
                    throw new RuntimeException("CPF já utilizado por outro usuário.");
                }
            });
        }
    }

    @Transactional
    @Override
    public void criarUsuarioAdminNoTenant(Cliente cliente, Usuario usuario) {

        String senhaGerada = gerarSenhaAleatoria(10);
        usuario.setSenha(passwordEncoder.encode(senhaGerada));
        usuario.setAtivo(true);
        usuario.setDataCriacao(LocalDateTime.now());

        Perfil admin = perfilService.findByDescricao("ADMIN");
        usuario.setPerfis(List.of(admin));

        repository.save(usuario);
        TenantContext.setCurrentTenant("public");
        repository.registrarUsuarioNoPublic(
                usuario.getEmail(),
                cliente.getId(),
                cliente.getSchema()
        );
    }

    @Override
    public void alterarSenha(AlterarSenhaRequestDTO dto) {

        var encoder = new BCryptPasswordEncoder();

        Usuario usuario = getUsuarioLogado();

        if (!encoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual inválida");
        }

        if (encoder.matches(dto.getSenhaNova(), usuario.getSenha())) {
            throw new RuntimeException("A nova senha não pode ser igual à senha atual");
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!dto.getSenhaNova().matches(regex)) {
            throw new RuntimeException(
                    "A nova senha deve ter no mínimo 8 caracteres, contendo pelo menos 1 letra maiúscula, 1 letra minúscula e 1 número."
            );
        }

        usuario.setSenha(encoder.encode(dto.getSenhaNova()));

        if (Boolean.TRUE.equals(usuario.getPrimeiroAcesso())) {
            usuario.setPrimeiroAcesso(false);
        }

        repository.save(usuario);
    }


    public Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByEmail(email);
    }
}
