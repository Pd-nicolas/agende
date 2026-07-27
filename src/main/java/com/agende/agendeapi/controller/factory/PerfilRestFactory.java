package com.agende.agendeapi.controller.factory;

import com.agende.agendeapi.controller.dto.request.PerfilRequestDTO;
import com.agende.agendeapi.controller.dto.response.PerfilResponseDTO;
import com.agende.agendeapi.entity.Perfil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PerfilRestFactory {

    public static Perfil getEntity(PerfilRequestDTO requestDTO) {
        Perfil perfil = new Perfil();
        perfil.setId(requestDTO.getId());
        return perfil;
    }

    public static List<Perfil> getEntityList(List<PerfilRequestDTO> listaDTO) {
        if (listaDTO == null || listaDTO.isEmpty()) {
            return new ArrayList<>();
        }
        return listaDTO.stream()
                .map(PerfilRestFactory::getEntity)
                .collect(Collectors.toList());
    }

    public static PerfilResponseDTO getResponse(Perfil perfil) {
        return new PerfilResponseDTO(perfil.getId(), perfil.getDescricao());
    }

    public static List<PerfilResponseDTO> getResponseList(List<Perfil> perfis) {
        if (perfis == null || perfis.isEmpty()) {
            return new ArrayList<>();
        }
        return perfis.stream()
                .map(PerfilRestFactory::getResponse)
                .collect(Collectors.toList());
    }

    public static List<Perfil> getEntityListFromIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return ids.stream()
                .map(id -> {
                    Perfil perfil = new Perfil();
                    perfil.setId(id);
                    return perfil;
                })
                .collect(Collectors.toList());
    }
}
