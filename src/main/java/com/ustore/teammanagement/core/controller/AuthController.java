package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.config.AuthAPI;
import com.ustore.teammanagement.core.service.PasswordResetService;
import com.ustore.teammanagement.payload.dto.request.LoginRequest;
import com.ustore.teammanagement.payload.dto.request.PasswordRecoveryRequest;
import com.ustore.teammanagement.payload.dto.request.ResetPasswordRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthAPI {

    private final PasswordResetService passwordResetService;

    public AuthController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest dto) {
        String token = passwordResetService.authenticationLogin(dto);
        return ResponseEntity.ok(token);
    }

    @Override
    @PostMapping("/password-recovery")
    public ResponseEntity<String> forgotPassword(@RequestBody PasswordRecoveryRequest request) {
        String token = passwordResetService.passwordRecovery(request.email());
        return ResponseEntity.ok("Token gerado e enviado para o e-mail: " + token);
    }

    @Override
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword(), request.confirmPassword());
        return ResponseEntity.ok().build();
    }
}

