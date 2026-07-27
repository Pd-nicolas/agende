package com.agende.agendeapi.controller.factory;

import com.agende.agendeapi.controller.dto.request.ClienteRequestDTO;
import com.agende.agendeapi.controller.dto.response.ClienteResponseDTO;
import com.agende.agendeapi.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClienteRestFactory {

    public static Cliente getEntity(ClienteRequestDTO requestDTO) {
        Cliente cliente = new Cliente();
        cliente.setNome(requestDTO.getNomeEmpresa());
        cliente.setSchema(requestDTO.getSlug());
        cliente.setCodigo(requestDTO.getSlug());
        cliente.setDescricaoEmpresa(requestDTO.getDescricaoEmpresa());

        return cliente;
    }

    public static ClienteResponseDTO getResponse(Cliente cliente) {
        ClienteResponseDTO responseDTO = new ClienteResponseDTO();
        responseDTO.setId(cliente.getId());
        responseDTO.setCodigo(cliente.getCodigo());
        responseDTO.setNome(cliente.getNome());
        responseDTO.setSchema(cliente.getSchema());
        responseDTO.setDescricaoEmpresa(cliente.getDescricaoEmpresa());
        responseDTO.setAtivo(cliente.getAtivo());

        return responseDTO;
    }

    public static List<Cliente> getEntityListFromIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(id -> {
                    Cliente cliente = new Cliente();
                    cliente.setId(id);
                    return cliente;
                })
                .collect(Collectors.toList());
    }

    public static Page<ClienteResponseDTO> getResponseListPage(Page<Cliente> clientes) {
        List<ClienteResponseDTO> responseDTO = clientes.getContent().stream().map(ClienteRestFactory::getResponse).toList();
        return new PageImpl<>(responseDTO, clientes.getPageable(), clientes.getTotalElements());
    }
}
