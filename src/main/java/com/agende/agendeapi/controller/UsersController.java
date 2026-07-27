package com.agende.agendeapi.controller;

import com.agende.agendeapi.controller.dto.request.AlterarSenhaRequestDTO;
import com.agende.agendeapi.controller.dto.request.PrimeiroAcessoRequestDTO;
import com.agende.agendeapi.controller.dto.request.UsuarioCadastroRequestDTO;
import com.agende.agendeapi.controller.dto.request.UsuarioEdicaoRequestDTO;
import com.agende.agendeapi.controller.dto.response.ExisteUsuarioAdminResponseDTO;
import com.agende.agendeapi.controller.dto.response.UsuarioCadastroResponseDTO;
import com.agende.agendeapi.controller.factory.UsuarioRestFactory;
import com.agende.agendeapi.entity.Usuario;
import com.agende.agendeapi.service.interfaces.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/usuario", produces = "application/json")
@Tag(name = "Usuário", description = "Gerenciamento de usuários")
public class UsersController {

    private final IUsuarioService service;

    public UsersController(IUsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar novo usuário", description = "Cadastra um novo usuário no sistema (apenas 'ADMIN' pode executar esta ação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioCadastroResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/cadastrar")
    @Parameters({
            @Parameter(ref = "X-Tenant-ID")
    })
    public ResponseEntity<UsuarioCadastroResponseDTO> cadastrar(
            @RequestBody @Valid UsuarioCadastroRequestDTO dto) {
        Usuario usuario = UsuarioRestFactory.getEntity(dto);
        return ResponseEntity.ok(UsuarioRestFactory.getResponse(service.criarUsuario(usuario)));
    }

    @Operation(summary = "Cadastrar primeiro usuário (Administrador)", description = "Utilizado para cadastrar o primeiro usuário do sistema, obrigatoriamente ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Primeiro usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou tentativa de cadastro duplicado")
    })
    @PostMapping("/primeiro-acesso")
    public ResponseEntity<UsuarioCadastroResponseDTO> cadastrarPrimeiroAcesso(
            @RequestBody @Valid PrimeiroAcessoRequestDTO dto) {
        Usuario usuario = UsuarioRestFactory.getEntityPrimeiroAcesso(dto);
        return ResponseEntity.ok(UsuarioRestFactory.getResponse(service.criarPrimeiroUsuario(usuario)));
    }

    @Operation(
            summary = "Listar usuários paginados",
            description = "Retorna uma lista paginada de usuários. Permite filtros opcionais por nome, e-mail, CPF ou perfil. Acesso restrito a usuários com perfil ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Apenas usuários ADMIN podem acessar este recurso."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @Parameters({
            @Parameter(ref = "X-Tenant-ID")
    })
    public ResponseEntity<Page<UsuarioCadastroResponseDTO>> listarUsuariosPaginados(
            @Parameter(description = "Número da página (começando em 0)", example = "0")
            @RequestParam(defaultValue = "0") Integer paginaAtual,

            @Parameter(description = "Quantidade de registros por página", example = "10")
            @RequestParam(defaultValue = "10") Integer tamanhoPagina,

            @Parameter(description = "Direção da ordenação: 'asc' ou 'desc'", example = "asc")
            @RequestParam(defaultValue = "asc") String direcao,

            @Parameter(description = "Campo pelo qual será feita a ordenação", example = "nome")
            @RequestParam(defaultValue = "nome") String ordenacao,

            @Parameter(description = "Filtro opcional por nome, email, CPF ou perfil", example = "admin")
            @RequestParam(defaultValue = "", required = false) String filtro) {

        Page<Usuario> usuarios = service.listarUsuariosPaginados(
                paginaAtual, tamanhoPagina, direcao, ordenacao, filtro);

        return ResponseEntity.ok(UsuarioRestFactory.getListPageResponseDTO(usuarios));
    }


    @Operation(summary = "Atualizar dados do usuário", description = "Atualiza as informações de um usuário. Acesso restrito a ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(ref = "X-Tenant-ID")
    })
    public ResponseEntity<UsuarioCadastroResponseDTO> atualizar(@RequestBody @Valid UsuarioEdicaoRequestDTO dto,
                                                                @PathVariable Long id) {
        Usuario usuario = UsuarioRestFactory.getEntityEdicao(dto);
        return ResponseEntity.ok(UsuarioRestFactory.getResponse(service.editarUsuario(usuario, id)));
    }

    @Operation(summary = "Inativar usuário", description = "Inativa (desativa) um usuário pelo id. Acesso restrito a ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário inativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "id inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/inativar")
    @Parameters({
            @Parameter(ref = "X-Tenant-ID")
    })
    public ResponseEntity<UsuarioCadastroResponseDTO> inativarUsuario(
            @Parameter(description = "id do usuário a ser inativado", required = true) @RequestParam Long id) {
        return ResponseEntity.ok(UsuarioRestFactory.getResponse(service.excluirUsuario(id)));
    }

    @Operation(summary = "Ativar usuário", description = "Ativa um usuário pelo id. Acesso restrito a ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário inativado com sucesso"),
            @ApiResponse(responseCode = "400", description = "id inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @Parameters({
            @Parameter(ref = "X-Tenant-ID")
    })
    @PreAuthorize("hasAuthority('ADMIN')")

    @PatchMapping("/ativar")
    public ResponseEntity<UsuarioCadastroResponseDTO> ativarUsuario(
            @Parameter(description = "id do usuário a ser ativado", required = true) @RequestParam Long id) {
        return ResponseEntity.ok(UsuarioRestFactory.getResponse(service.ativarUsuario(id)));
    }

    @Operation(summary = "Verificar existência de usuário ADMIN", description = "Verifica se já existe pelo menos um usuário com perfil ADMIN cadastrado.")
    @ApiResponse(responseCode = "200", description = "Resultado da verificação",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ExisteUsuarioAdminResponseDTO.class)))
    @GetMapping("/buscar-admin")
    public ResponseEntity<ExisteUsuarioAdminResponseDTO> bustarUsuarioAdmin() {
        return ResponseEntity.ok(UsuarioRestFactory.getBoolean(service.existsByPerfis_Id(1L)));
    }

    @Operation(
            summary = "Redefinir senha do usuário logado",
            description = "Permite que o usuário autenticado altere sua própria senha, informando a senha atual e a nova senha."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou senha atual incorreta"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PutMapping(value = "/senha/redefinir")
    public ResponseEntity<String> alterarSenha(
            @Validated @RequestBody AlterarSenhaRequestDTO dto) {

        service.alterarSenha(dto);
        return ResponseEntity.ok("Senha alterada com Sucesso!");
    }
}
