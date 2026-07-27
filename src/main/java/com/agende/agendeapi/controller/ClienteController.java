package com.agende.agendeapi.controller;

import com.agende.agendeapi.controller.dto.request.ClienteRequestDTO;
import com.agende.agendeapi.controller.dto.response.ClienteResponseDTO;
import com.agende.agendeapi.controller.factory.ClienteRestFactory;
import com.agende.agendeapi.controller.factory.UsuarioRestFactory;
import com.agende.agendeapi.entity.Cliente;
import com.agende.agendeapi.entity.Usuario;
import com.agende.agendeapi.service.interfaces.IClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cliente")
@Tag(name = "Cliente", description = "Gerenciamento de clientes (apenas SUPER_ADMIN)")
public class ClienteController {

    private final IClienteService service;

    public ClienteController(IClienteService service) {
        this.service = service;
    }


    @Operation(
            summary = "Cadastrar novo cliente",
            description = "Cria um novo cliente no sistema e registra automaticamente um usuário administrador para esse cliente. Acesso restrito a usuários com o perfil SUPER_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClienteResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Apenas SUPER_ADMIN pode executar esta ação.", content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ClienteResponseDTO> criar(
            @RequestBody ClienteRequestDTO clienteRequestDTO) {

        Cliente cliente = ClienteRestFactory.getEntity(clienteRequestDTO);
        Usuario usuario = UsuarioRestFactory.getEntityTenant(clienteRequestDTO);

        return ResponseEntity.ok(
                ClienteRestFactory.getResponse(service.criarClienteComAdmin(cliente, usuario))
        );
    }


    @Operation(
            summary = "Listar clientes paginados",
            description = "Retorna uma lista paginada de clientes cadastrados no sistema. Acesso restrito a usuários SUPER_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Apenas SUPER_ADMIN pode acessar este recurso."),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> getListPage(
            @Parameter(description = "Número da página (começando em 0)", example = "0")
            @RequestParam(defaultValue = "0") Integer paginaAtual,

            @Parameter(description = "Quantidade de registros por página", example = "10")
            @RequestParam(defaultValue = "10") Integer tamanhoPagina,

            @Parameter(description = "Direção da ordenação: 'asc' ou 'desc'", example = "asc")
            @RequestParam(defaultValue = "asc") String direcao,

            @Parameter(description = "Campo pelo qual será feita a ordenação", example = "nome")
            @RequestParam(defaultValue = "nome") String ordenacao,

            @Parameter(description = "Filtro opcional por nome", example = "agende")
            @RequestParam(defaultValue = "", required = false) String filtro
    ) {
        Page<Cliente> clientes =
                service.listarClientesPaginados(paginaAtual, tamanhoPagina, direcao, ordenacao, filtro);

        return ResponseEntity.ok(ClienteRestFactory.getResponseListPage(clientes));
    }


    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna os dados de um cliente específico pelo seu ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ClienteRestFactory.getResponse(service.buscarPorId(id))
        );
    }


    @Operation(
            summary = "Ativar/Inativar cliente",
            description = "Altera o status de um cliente (ativo/inativo). Acesso restrito a usuários SUPER_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status do cliente alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Apenas SUPER_ADMIN pode executar esta ação.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content)
    })
    @PatchMapping("/ativar-inativar/{id}")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<ClienteResponseDTO> ativarInativar(
            @Parameter(description = "ID do cliente", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Define se o cliente ficará ativo (true) ou inativo (false)", example = "true")
            @RequestParam Boolean ativo
    ) {
        return ResponseEntity.ok(
                ClienteRestFactory.getResponse(service.ativarInativarCliente(id, ativo))
        );
    }
}
