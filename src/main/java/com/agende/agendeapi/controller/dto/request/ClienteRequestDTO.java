package com.agende.agendeapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Schema(description = "DTO utilizado para cadastrar um novo cliente.")
@Getter
@Setter
public class ClienteRequestDTO {

    @Schema(description = "Nome do cliente.", example = "Agende")
    @NotNull(message = "O campo nome deve ser preenchido")
    private String nomeEmpresa;

    @Schema(description = "Nome do schema.", example = "Agende")
    @NotNull(message = "O campo schema deve ser preenchido")
    private String slug;

    @Schema(description = "Descrição da empresa", example = "Agende")
    @NotNull(message = "O campo schema deve ser preenchido")
    private String descricaoEmpresa;

    @Schema(description = "Nome completo do usuário.", example = "João Silva")
    @NotNull(message = "O campo nome deve ser preenchido")
    private String nome;

    @Schema(description = "Endereço de e-mail do usuário.", example = "joao.silva@email.com")
    @NotNull(message = "O campo email deve ser preenchido")
    @Email(message = "Email inválido")
    private String email;

    @Schema(description = "CPF do usuário (Cadastro de Pessoa Física).", example = "12345678909")
    @CPF(message = "CPF inválido")
    private String cpf;
}
