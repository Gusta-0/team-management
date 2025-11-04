package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.config.AuthAPI;
import com.ustore.teammanagement.core.service.AuthenticationServiceImpl;
import com.ustore.teammanagement.core.service.PasswordRecoveryService;
import com.ustore.teammanagement.payload.dto.request.ForgotPasswordRequest;
import com.ustore.teammanagement.payload.dto.request.LoginRequest;
import com.ustore.teammanagement.payload.dto.request.ResetPasswordRequest;
import com.ustore.teammanagement.payload.dto.response.RecoveryTokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthAPI{

    private final AuthenticationServiceImpl authenticationService;
    private final PasswordRecoveryService recoveryService;

    public AuthController(AuthenticationServiceImpl authenticationService,
                          PasswordRecoveryService recoveryService) {
        this.authenticationService = authenticationService;
        this.recoveryService = recoveryService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequest request) {
        String accessToken = authenticationService.generateAccessToken(request);
        String refreshToken = authenticationService.generateRefreshToken(request); // opcional

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")                   // Disponível em todo o domínio
                .sameSite("Strict")          // Previne envio entre domínios
                .maxAge(Duration.ofHours(1))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(Map.of("message", "Login successful",
                        "accessToken", accessToken));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<RecoveryTokenResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        RecoveryTokenResponse response = recoveryService.generateRecoveryToken(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        recoveryService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}

