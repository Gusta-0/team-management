package com.ustore.teammanagement.config;


import com.ustore.teammanagement.payload.dto.request.LoginRequest;
import com.ustore.teammanagement.payload.dto.request.PasswordRecoveryRequest;
import com.ustore.teammanagement.payload.dto.request.ResetPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Autenticação", description = "Endpoints para login e recuperação de senha")
public interface AuthAPI {

    @Operation(
            summary = "Login do usuário",
            description = "Autentica o usuário com email e senha e retorna um token JWT em formato de String"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<String> login(
            @Parameter(description = "Dados de login do usuário", required = true)
            @Valid @RequestBody LoginRequest request
    );


    @Operation(
            summary = "Solicitar recuperação de senha",
            description = "Gera um token de recuperação e simula o envio por e-mail."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    ResponseEntity<String> forgotPassword(@RequestBody PasswordRecoveryRequest request);

    @Operation(
            summary = "Redefinir senha",
            description = "Permite redefinir a senha com base em um token válido e confirmação da nova senha."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token inválido ou senhas não coincidem"),
            @ApiResponse(responseCode = "404", description = "Token não encontrado ou expirado")
    })
    ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request);
}

