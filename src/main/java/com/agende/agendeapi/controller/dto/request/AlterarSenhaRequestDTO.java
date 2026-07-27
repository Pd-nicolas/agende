package com.agende.agendeapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "DTO utilizado para redefinir a senha do usuário autenticado.")
@Getter
@Setter
public class AlterarSenhaRequestDTO {

    @Schema(
            description = "Senha atual do usuário.",
            example = "MinhaSenha@123"
    )
    @NotEmpty(message = "É necessário preencher a Senha Atual! ")
    private String senhaAtual;

    @Schema(
            description = "Nova senha que será definida para o usuário.",
            example = "NovaSenha@456"
    )
    @NotEmpty(message = "É necessário preencher a Senha Nova! ")
    private String senhaNova;
}
