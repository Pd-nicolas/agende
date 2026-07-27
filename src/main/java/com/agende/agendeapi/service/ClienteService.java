package com.agende.agendeapi.service;

import com.agende.agendeapi.entity.Cliente;
import com.agende.agendeapi.entity.Usuario;
import com.agende.agendeapi.hibernate.TenantContext;
import com.agende.agendeapi.repository.ClienteRepository;
import com.agende.agendeapi.service.interfaces.IClienteService;
import com.agende.agendeapi.service.interfaces.IUsuarioService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService implements IClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;
    private final TenantService tenantService;
    private final IUsuarioService usuarioService;

    public ClienteService(ClienteRepository clienteRepository,
                          @Lazy TenantService tenantService,
                          IUsuarioService usuarioService) {
        this.clienteRepository = clienteRepository;
        this.tenantService = tenantService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Cliente criarCliente(Cliente cliente) {
        logger.info("Iniciando o criacao do Cliente " + cliente.getNome());
        clienteRepository.save(cliente);

        tenantService.createAndMigrateTenant(cliente.getSchema());

        return cliente;
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o Id: " + id));
    }

    public Cliente buscarPorSchema(String schema) {
        return clienteRepository.findBySchema(schema).orElse(null);
    }

    @Override
    public Cliente criarClienteComAdmin(Cliente cliente, Usuario usuario) {
        Cliente clienteExistente = buscarPorSchema(cliente.getSchema());

        if (clienteExistente != null && cliente.getSchema().equals(clienteExistente.getSchema())) {
            throw new RuntimeException("Esse schema já existe, por favor escollha outro nome.");
        }

        Cliente clienteCriado = criarCliente(cliente);

        TenantContext.setCurrentTenant(cliente.getSchema());
        try {
            usuarioService.criarUsuarioAdminNoTenant(clienteCriado, usuario);
        } finally {
            TenantContext.clear();
        }

        return cliente;
    }

    @Override
    public Page<Cliente> listarClientesPaginados(Integer paginaAtual, Integer tamanhoPagina, String direcao,
                                                 String ordenacao, String filtro) {
        Sort sort = Sort.by(Sort.Direction.fromString(direcao), ordenacao);
        Pageable pageable = PageRequest.of(paginaAtual, tamanhoPagina, sort);
        return clienteRepository.findClientesFiltrados(filtro, pageable);
    }

    @Override
    public Cliente ativarInativarCliente(Long id, Boolean ativo) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(ativo);
        return clienteRepository.save(cliente);
    }

    public List<Cliente> findAllAtivos() {
        return clienteRepository.findAllByAtivoIsTrue();
    }

    @Override
    public void atualizarTenant() {
        tenantService.migrateAllTenants();
    }
}

