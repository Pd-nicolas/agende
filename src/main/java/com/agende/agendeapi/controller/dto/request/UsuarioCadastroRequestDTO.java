package com.agende.agendeapi.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

@Schema(description = "DTO utilizado para cadastro de um novo usuário.")
@Getter
@Setter
public class UsuarioCadastroRequestDTO {

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

    @Schema(description = "Perfis ou papéis atribuídos ao usuário.")
    private List<Long> perfil;

    @Schema(description = "Lista de IDs dos clientes que o usuário poderá acessar.")
    private List<Long> clientes;

}
