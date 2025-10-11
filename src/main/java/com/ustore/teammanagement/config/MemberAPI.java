package com.ustore.teammanagement.config;

import com.ustore.teammanagement.enums.MemberStatus;
import com.ustore.teammanagement.enums.Role;
import com.ustore.teammanagement.payload.dto.request.MemberRequest;
import com.ustore.teammanagement.payload.dto.request.MemberUpdateRequest;
import com.ustore.teammanagement.payload.dto.response.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Tag(name = "Members", description = "Member management endpoints")
public interface MemberAPI {

    @Operation(summary = "Salvar novo colaborador",
            description = "Adiciona um novo colaborador na equipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colaborador adicionado com sucesso",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MemberResponse> saveMember(@Valid @RequestBody MemberRequest memberRequest);

    @Operation(summary = "Pesquisar colaboradores",
            description = "Pesquisa colaboradores por nome e/ou email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de colaboradores encontrada")
    })
    public Page<MemberResponse> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable
    );

    @Operation(summary = "Filtrar colaboradores",
            description = "Filtra colaboradores por Departamento, Status e Nível de Acesso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de colaboradores encontrada")
    })
    public Page<MemberResponse> filtrar(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) MemberStatus status,
            @RequestParam(required = false) Role role,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "Atualizar dados do colaborador",
            description = "Atualiza os dados de um colaborador existente. " +
                    "Usuários comuns só podem alterar os próprios dados. " +
                    "Admins e Gerentes podem alterar qualquer colaborador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Colaborador atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar este colaborador",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Colaborador não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MemberResponse> updateMember(
            @Parameter(description = "ID do colaborador a ser atualizado", required = true)
            @PathVariable UUID id,
            @RequestBody @Valid MemberUpdateRequest dto
    ) throws AccessDeniedException;

    @Operation(
            summary = "Inativar colaborador",
            description = "Inativa um colaborador (delete lógico). " +
                    "Apenas Admins e Gerentes têm permissão para esta ação."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Colaborador inativado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para inativar este colaborador",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Colaborador não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public void inactivate(@PathVariable UUID id) throws AccessDeniedException;
}
