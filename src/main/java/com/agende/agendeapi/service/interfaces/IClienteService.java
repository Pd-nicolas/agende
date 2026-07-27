package com.agende.agendeapi.service.interfaces;

import com.agende.agendeapi.entity.Cliente;
import com.agende.agendeapi.entity.Usuario;
import org.springframework.data.domain.Page;

public interface IClienteService {
    Cliente buscarPorId(Long id);

    Cliente criarClienteComAdmin(Cliente cliente, Usuario usuario);

    Page<Cliente> listarClientesPaginados(Integer paginaAtual, Integer tamanhoPagina, String direcao,
                                          String ordenacao, String filtro);

    Cliente ativarInativarCliente(Long id, Boolean ativo);

    void atualizarTenant();
}
