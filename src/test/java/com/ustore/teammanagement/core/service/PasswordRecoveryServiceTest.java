package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.entity.PasswordRecoveryToken;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.PasswordRecovery;
import com.ustore.teammanagement.payload.dto.request.ForgotPasswordRequest;
import com.ustore.teammanagement.payload.dto.request.ResetPasswordRequest;
import com.ustore.teammanagement.payload.dto.response.RecoveryTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordRecovery tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordRecoveryService recoveryService;

    @Test
    @DisplayName("Deve gerar um token de recuperação e salvar no repositório")
    void shouldGenerateRecoveryTokenSuccessfully() {

        ForgotPasswordRequest request = new ForgotPasswordRequest("test@example.com");

        Member member = new Member();
        member.setId(UUID.randomUUID());
        member.setEmail("test@example.com");

        when(memberRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(member));

        ArgumentCaptor<PasswordRecoveryToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordRecoveryToken.class);


        RecoveryTokenResponse response = recoveryService.generateRecoveryToken(request);

        assertNotNull(response);
        assertNotNull(response.recoveryToken());
        assertTrue(response.recoveryToken().length() > 20, "Token deve ser seguro e aleatório");

        verify(memberRepository, times(1)).findByEmail("test@example.com");

        verify(tokenRepository, times(1)).save(tokenCaptor.capture());

        PasswordRecoveryToken savedToken = tokenCaptor.getValue();

        assertEquals(member, savedToken.getMember());
        assertEquals(response.recoveryToken(), savedToken.getToken());

        assertFalse(savedToken.isUsed(), "Token não deve estar marcado como usado");
        assertNotNull(savedToken.getExpiration());

        assertTrue(savedToken.getExpiration().isAfter(LocalDateTime.now()),
                "A expiração deve ser no futuro");
        assertTrue(savedToken.getExpiration().isBefore(LocalDateTime.now().plusHours(1)),
                "A expiração deve ser aproximadamente 30 minutos à frente");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o e-mail não existir")
    void shouldThrowWhenEmailNotFound() {

        ForgotPasswordRequest request = new ForgotPasswordRequest("notfound@example.com");

        when(memberRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> recoveryService.generateRecoveryToken(request));

        verify(memberRepository, times(1)).findByEmail("notfound@example.com");
        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve redefinir a senha com um token válido e não expirado")
    void shouldResetPasswordWithValidToken() {
        ResetPasswordRequest request =
                new ResetPasswordRequest("valid-token", "NewPassword123!");

        Member member = new Member();
        member.setId(UUID.randomUUID());

        PasswordRecoveryToken token = new PasswordRecoveryToken();
        token.setToken("valid-token");
        token.setUsed(false);
        token.setMember(member);
        token.setExpiration(LocalDateTime.now().plusMinutes(10));

        when(tokenRepository.findByTokenAndUsedFalse("valid-token"))
                .thenReturn(Optional.of(token));

        when(passwordEncoder.encode("NewPassword123!"))
                .thenReturn("encoded-password");

        recoveryService.resetPassword(request);

        verify(passwordEncoder).encode("NewPassword123!");
        assertEquals("encoded-password", member.getPassword());

        verify(memberRepository).save(member);

        assertTrue(token.isUsed());
        verify(tokenRepository).save(token);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o token for inválido ou já usado")
    void shouldThrowWhenTokenNotFoundOrUsed() {

        ResetPasswordRequest request =
                new ResetPasswordRequest("invalid-token", "NewPassword123!");

        when(tokenRepository.findByTokenAndUsedFalse("invalid-token"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> recoveryService.resetPassword(request));

        verify(memberRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o token estiver expirado")
    void shouldThrowWhenTokenExpired() {

        ResetPasswordRequest request =
                new ResetPasswordRequest("expired-token", "NewPassword123!");

        Member member = new Member();
        member.setId(UUID.randomUUID());

        PasswordRecoveryToken token = new PasswordRecoveryToken();
        token.setToken("expired-token");
        token.setUsed(false);
        token.setMember(member);
        token.setExpiration(LocalDateTime.now().minusMinutes(5)); // EXPIROU

        when(tokenRepository.findByTokenAndUsedFalse("expired-token"))
                .thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class,
                () -> recoveryService.resetPassword(request));

        verify(memberRepository, never()).save(any());
        verify(tokenRepository, never()).save(any());
    }
}
