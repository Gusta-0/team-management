package com.ustore.teammanagement.core.controller;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.service.AuthenticationServiceImpl;
import com.ustore.teammanagement.core.service.PasswordRecoveryService;
import com.ustore.teammanagement.core.service.TokenService;
import com.ustore.teammanagement.payload.dto.request.ForgotPasswordRequest;
import com.ustore.teammanagement.payload.dto.request.LoginRequest;
import com.ustore.teammanagement.payload.dto.request.ResetPasswordRequest;
import com.ustore.teammanagement.payload.dto.response.LoginResponse;
import com.ustore.teammanagement.payload.dto.response.RecoveryTokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordRecoveryService recoveryService;

    @Test
    void mustLogInSuccessfully() {
        LoginRequest request = new LoginRequest("user@test.com", "123");

        Member member = new Member();
        member.setId(UUID.randomUUID());
        member.setEmail("user@test.com");

        when(authenticationService.authenticate(request)).thenReturn(member);
        when(tokenService.generateToken(member)).thenReturn("token-acesso");
        when(tokenService.generateRefreshToken(member)).thenReturn("token-refresh");

        ResponseEntity<LoginResponse> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("token-acesso", response.getBody().accessToken());
        assertEquals("token-refresh", response.getBody().refreshToken());
        assertEquals("Bearer", response.getBody().tokenType());

        verify(authenticationService).authenticate(request);
        verify(tokenService).generateToken(member);
        verify(tokenService).generateRefreshToken(member);
    }

    @Test
    void mustGenerateRecoveryToken() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("user@test.com");

        RecoveryTokenResponse expectedResponse =
                new RecoveryTokenResponse("token123");

        when(recoveryService.generateRecoveryToken(request))
                .thenReturn(expectedResponse);

        ResponseEntity<RecoveryTokenResponse> response = authController.forgotPassword(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());

        verify(recoveryService).generateRecoveryToken(request);
    }

    @Test
    void mustResetPasswordSuccessfully() {
        ResetPasswordRequest request =
                new ResetPasswordRequest("token123", "newPassword");

        doNothing().when(recoveryService).resetPassword(request);

        ResponseEntity<Void> response = authController.resetPassword(request);

        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(recoveryService).resetPassword(request);
    }
}