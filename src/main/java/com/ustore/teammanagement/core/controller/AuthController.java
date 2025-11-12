package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.config.AuthAPI;
import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.service.AuthenticationServiceImpl;
import com.ustore.teammanagement.core.service.PasswordRecoveryService;
import com.ustore.teammanagement.core.service.TokenService;
import com.ustore.teammanagement.payload.dto.request.ForgotPasswordRequest;
import com.ustore.teammanagement.payload.dto.request.LoginRequest;
import com.ustore.teammanagement.payload.dto.request.ResetPasswordRequest;
import com.ustore.teammanagement.payload.dto.response.LoginResponse;
import com.ustore.teammanagement.payload.dto.response.RecoveryTokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthAPI{

    private final AuthenticationServiceImpl authenticationService;
    private final PasswordRecoveryService recoveryService;
    private final TokenService tokenService;

    public AuthController(AuthenticationServiceImpl authenticationService,
                          PasswordRecoveryService recoveryService,
                          TokenService tokenService) {
        this.authenticationService = authenticationService;
        this.recoveryService = recoveryService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        Member member = authenticationService.authenticate(request);

        String access = tokenService.generateToken(member);
        String refresh = tokenService.generateRefreshToken(member);

        LoginResponse response = new LoginResponse(
                access,
                refresh,
                "Bearer",
                3600
        );

        return ResponseEntity.ok(response);
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

