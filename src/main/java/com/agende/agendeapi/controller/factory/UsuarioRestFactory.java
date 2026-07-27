package com.agende.agendeapi.controller.factory;

import com.agende.agendeapi.controller.dto.request.ClienteRequestDTO;
import com.agende.agendeapi.controller.dto.request.PrimeiroAcessoRequestDTO;
import com.agende.agendeapi.controller.dto.request.UsuarioCadastroRequestDTO;
import com.agende.agendeapi.controller.dto.request.UsuarioEdicaoRequestDTO;
import com.agende.agendeapi.controller.dto.response.ExisteUsuarioAdminResponseDTO;
import com.agende.agendeapi.controller.dto.response.UsuarioCadastroResponseDTO;
import com.agende.agendeapi.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UsuarioRestFactory {

    public static Usuario getEntity(UsuarioCadastroRequestDTO requestDTO) {
        Usuario usuario = new Usuario();
        usuario.setCpf(requestDTO.getCpf());
        usuario.setNome(requestDTO.getNome());
        usuario.setEmail(requestDTO.getEmail());

        if (requestDTO.getPerfil() != null && !requestDTO.getPerfil().isEmpty()) {
            usuario.setPerfis(PerfilRestFactory.getEntityListFromIds(requestDTO.getPerfil()));
            usuario.setClientes(ClienteRestFactory.getEntityListFromIds(requestDTO.getClientes()));
        } else {
            usuario.setPerfis(new ArrayList<>());
        }

        return usuario;
    }

    public static Usuario getEntityTenant(ClienteRequestDTO requestDTO) {
        Usuario usuario = new Usuario();
        usuario.setCpf(requestDTO.getCpf());
        usuario.setNome(requestDTO.getNome());
        usuario.setEmail(requestDTO.getEmail());

        return usuario;
    }

    public static UsuarioCadastroResponseDTO getResponse(Usuario usuario) {
        UsuarioCadastroResponseDTO responseDTO = new UsuarioCadastroResponseDTO();
        responseDTO.setCpf(usuario.getCpf());
        responseDTO.setNome(usuario.getNome());
        responseDTO.setAtivo(usuario.getAtivo());
        responseDTO.setId(usuario.getId());
        responseDTO.setEmail(usuario.getEmail());

        responseDTO.setPerfil(PerfilRestFactory.getResponseList(usuario.getPerfis()));

        return responseDTO;
    }

    public static Page<UsuarioCadastroResponseDTO> getListPageResponseDTO(Page<Usuario> usuarios) {
        List<UsuarioCadastroResponseDTO> responseDTO = usuarios.stream().map(UsuarioRestFactory::getResponse).collect(Collectors.toList());
        return new PageImpl<>(responseDTO, usuarios.getPageable(), usuarios.getTotalElements());
    }

    public static Usuario getEntityEdicao(UsuarioEdicaoRequestDTO requestDTO) {
        Usuario usuario = new Usuario();
        usuario.setNome(requestDTO.getNome());
        usuario.setEmail(requestDTO.getEmail());
        usuario.setCpf(requestDTO.getCpf());

        if (requestDTO.getPerfil() != null && !requestDTO.getPerfil().isEmpty()) {
            usuario.setPerfis(PerfilRestFactory.getEntityListFromIds(requestDTO.getPerfil()));
        } else {
            usuario.setPerfis(new ArrayList<>());
        }

        return usuario;
    }

    public static ExisteUsuarioAdminResponseDTO getBoolean(Boolean retorno) {
        return new ExisteUsuarioAdminResponseDTO(retorno);
    }

    public static Usuario getEntityPrimeiroAcesso(PrimeiroAcessoRequestDTO requestDTO) {
        Usuario usuario = new Usuario();
        usuario.setCpf(requestDTO.getCpf());
        usuario.setNome(requestDTO.getNome());
        usuario.setEmail(requestDTO.getEmail());

        return usuario;
    }
}
