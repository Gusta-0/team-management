package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.payload.dto.request.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    @DisplayName("Deve autenticar usuário e retornar Member com sucesso")
    void shouldAuthenticateUserSuccessfully() {

        LoginRequest request = new LoginRequest("user@example.com", "123456");

        Member member = new Member();
        member.setId(UUID.randomUUID());
        member.setEmail("user@example.com");

        Authentication authenticationMock = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);

        when(memberRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(member));

        Member result = authenticationService.authenticate(request);

        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(memberRepository, times(1))
                .findByEmail("user@example.com");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não for encontrado no banco")
    void shouldThrowExceptionWhenUserNotFound() {

        LoginRequest request = new LoginRequest("notfound@example.com", "123456");

        Authentication authenticationMock = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);

        when(memberRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.authenticate(request);
        });

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(memberRepository, times(1))
                .findByEmail("notfound@example.com");
    }
}
