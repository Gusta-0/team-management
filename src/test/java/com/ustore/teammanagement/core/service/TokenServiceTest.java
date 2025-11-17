package com.ustore.teammanagement.core.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TokenServiceTest {

    private MemberRepository memberRepository;
    private TokenService tokenService;
    private Member member;

    private final String SECRET = "super-secret-key-123";

    @BeforeEach
    void setup() {
        memberRepository = Mockito.mock(MemberRepository.class);
        tokenService = new TokenService(memberRepository);

        ReflectionTestUtils.setField(tokenService, "securityJwt", SECRET);

        member = new Member();
        member.setId(UUID.randomUUID());
        member.setEmail("user@example.com");
        member.setName("User Test");
    }

    @Test
    @DisplayName("Deve gerar token JWT válido")
    void generateToken_success() {
        String token = tokenService.generateToken(member);

        assertNotNull(token);
        String subject = JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer("Team Management App")
                .withClaim("type", "access")
                .build()
                .verify(token)
                .getSubject();

        assertEquals("user@example.com", subject);
    }

    @Test
    @DisplayName("Deve extrair o subject corretamente de um token válido")
    void getSubject_success() {
        String token = tokenService.generateToken(member);

        String subject = tokenService.getSubject(token);

        assertEquals("user@example.com", subject);
    }

    @Test
    @DisplayName("Deve lançar erro ao extrair subject de token inválido")
    void getSubject_invalidToken() {
        assertThrows(Exception.class, () -> tokenService.getSubject("invalid-token-123"));
    }

    @Test
    @DisplayName("Deve gerar refresh token válido")
    void generateRefreshToken_success() {
        String refreshToken = tokenService.generateRefreshToken(member);

        assertNotNull(refreshToken);

        var jwt = JWT.require(Algorithm.HMAC256(SECRET))
                .withIssuer("Team Management App")
                .withClaim("type", "refresh")
                .build()
                .verify(refreshToken);

        assertEquals("user@example.com", jwt.getSubject());
    }

    @Test
    @DisplayName("Deve gerar novo access token ao fornecer refresh token válido")
    void refreshAccessToken_success() {
        String refreshToken = JWT.create()
                .withIssuer("Team Management App")
                .withSubject("user@example.com")
                .withClaim("type", "refresh")
                .sign(Algorithm.HMAC256(SECRET));

        when(memberRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(member));

        String newAccessToken = tokenService.refreshAccessToken(refreshToken);

        assertNotNull(newAccessToken);
    }

    @Test
    @DisplayName("Deve lançar erro se o e-mail do refresh token não existir")
    void refreshAccessToken_userNotFound() {
        String refreshToken = JWT.create()
                .withIssuer("Team Management App")
                .withSubject("notfound@example.com")
                .withClaim("type", "refresh")
                .sign(Algorithm.HMAC256(SECRET));

        when(memberRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> tokenService.refreshAccessToken(refreshToken));
    }
}