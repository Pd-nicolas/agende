package com.agende.agendeapi.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCadastroResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private Boolean ativo;
    private List<PerfilResponseDTO> perfil;
    private String token;
}
