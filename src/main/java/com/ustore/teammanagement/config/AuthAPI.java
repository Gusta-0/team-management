package com.ustore.teammanagement.config;


import com.ustore.teammanagement.payload.dto.request.*;
import com.ustore.teammanagement.payload.dto.response.RecoveryTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Tag(name = "Autenticação", description = "Endpoints para autenticação e renovação de tokens JWT")
public interface AuthAPI {
    @Operation(
            summary = "Autenticar usuário",
            description = "Realiza a autenticação do usuário e retorna um token JWT válido.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Autenticação realizada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = TokenForm.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
            }
    )
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequest request);


    @Operation(summary = "Solicita recuperação de senha",
            description = "Gera um token de recuperação para o email informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token gerado com sucesso",
                    content = @Content(schema = @Schema(implementation = RecoveryTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Email inválido ou não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping("/forgot-password")
    ResponseEntity<RecoveryTokenResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request
    );

    @Operation(summary = "Redefine senha do usuário",
            description = "Recebe o token de recuperação e a nova senha para redefinir a senha do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha redefinida com sucesso", content = @Content),
            @ApiResponse(responseCode = "400", description = "Token inválido ou expirado / senha inválida", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping("/reset-password")
    ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request
    );

}

