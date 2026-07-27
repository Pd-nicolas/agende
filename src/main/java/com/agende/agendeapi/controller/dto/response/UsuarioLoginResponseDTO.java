package com.agende.agendeapi.controller.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioLoginResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private Boolean ativo;
    private List<PerfilResponseDTO> perfil;
    private String token;
    private String schema;
    private Boolean primeiroAcesso;

}
