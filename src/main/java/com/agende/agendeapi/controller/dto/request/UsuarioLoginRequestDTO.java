package com.agende.agendeapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "DTO utilizado para autenticação/login do usuário.")
@Getter
@Setter
public class UsuarioLoginRequestDTO {

    @Schema(description = "Email do usuário que já possui um cadastro", example = "example@gmail.com")
    @NotBlank(message = "O campo email deve ser preenchido.")
    private String email;

    @Schema(description = "Senha do usuário.", example = "minhaSenhaSegura123")
    @NotBlank(message = "O campo senha deve ser preenchido.")
    private String senha;
}
