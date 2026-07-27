package com.agende.agendeapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "DTO utilizado para informar o perfil atribuído a um usuário.")
@Getter
@Setter
public class PerfilRequestDTO {

    @Schema(description = "Identificador único do perfil.", example = "1")
    @NotNull(message = "O campo id deve ser preenchido.")
    private Long id;
}

