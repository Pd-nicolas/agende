package com.agende.agendeapi.service.interfaces;

import com.agende.agendeapi.controller.dto.request.AlterarSenhaRequestDTO;
import com.agende.agendeapi.entity.Cliente;
import com.agende.agendeapi.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface IUsuarioService {
    @Transactional(readOnly = true)
    Usuario findByCpf(String cpf);

    @Transactional
    Usuario criarUsuario(Usuario usuario);

    @Transactional
    Usuario criarPrimeiroUsuario(Usuario usuario);

    Page<Usuario> listarUsuariosPaginados(Integer paginaAtual, Integer tamanhoPagina, String direcao,
                                          String ordenacao, String filtro);

    @Transactional
    Usuario editarUsuario(Usuario usuario, Long id);

    @Transactional
    Usuario excluirUsuario(Long id);

    @Transactional
    Usuario ativarUsuario(Long id);

    boolean existsByPerfis_Id(Long idperfil);

    Usuario findByEmail(String email);

    @Transactional
    void criarUsuarioAdminNoTenant(Cliente cliente, Usuario usuario);

    void alterarSenha(AlterarSenhaRequestDTO dto);
}
