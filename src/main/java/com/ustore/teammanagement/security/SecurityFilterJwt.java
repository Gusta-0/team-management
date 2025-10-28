package com.ustore.teammanagement.security;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilterJwt extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final MemberRepository memberRepository;

    public SecurityFilterJwt(TokenService tokenService, MemberRepository memberRepository) {
        this.tokenService = tokenService;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();


        if (path.startsWith("/auth/login") || path.startsWith("/auth/refresh-token")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> tokenJwtRequest = recoverTokenRequest(request);
        if (tokenJwtRequest.isPresent()) {
            String subjectToken = this.tokenService.getSubject(tokenJwtRequest.get());
            Member member = this.memberRepository.findByEmail(subjectToken)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Authentication auth = new UsernamePasswordAuthenticationToken(member, null, member.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> recoverTokenRequest(HttpServletRequest request) {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            return Optional.of(tokenHeader.substring(7)); // remove "Bearer "
        }
        return Optional.empty();
    }
}