package com.ustore.teammanagement.security;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityFilterJwtTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilterJwt securityFilterJwt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void mustAllowAccessForPublicRoutes() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/auth/login");
        when(request.getMethod()).thenReturn("POST");

        securityFilterJwt.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void mustFollowWithoutAuthenticationWhenNoToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/tasks");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn(null);

        securityFilterJwt.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void mustAuthenticateWhenTokenValid() throws Exception {
        when(request.getRequestURI()).thenReturn("/tasks");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer abc123");

        when(tokenService.getSubject("abc123")).thenReturn("user@test.com");

        Member member = new Member();
        member.setEmail("user@test.com");

        when(memberRepository.findByEmail("user@test.com")).thenReturn(Optional.of(member));

        securityFilterJwt.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(member, SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldReturn401WhenTokenInvalid() throws Exception {
        when(request.getRequestURI()).thenReturn("/tasks");
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("Authorization")).thenReturn("Bearer token_ruim");

        when(tokenService.getSubject("token_ruim")).thenThrow(new RuntimeException("Token inválido"));

        securityFilterJwt.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(any(), any());
    }
}