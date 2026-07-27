package com.agende.agendeapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

@Schema(description = "DTO utilizado para atualização/edição dos dados de um usuário existente.")
@Getter
@Setter
public class UsuarioEdicaoRequestDTO {

    @Schema(description = "Nome completo do usuário.", example = "Maria Oliveira")
    @NotNull(message = "O campo nome deve ser preenchido")
    private String nome;

    @Schema(description = "Endereço de e-mail do usuário.", example = "maria.oliveira@email.com")
    @NotNull(message = "O campo email deve ser preenchido")
    @Email(message = "Email inválido")
    private String email;

    @Schema(description = "CPF do usuário (Cadastro de Pessoa Física).", example = "98765432100")
    @CPF(message = "CPF inválido")
    private String cpf;

    @Schema(description = "Perfis ou papéis atribuídos ao usuário.")
    private List<Long> perfil;
}
