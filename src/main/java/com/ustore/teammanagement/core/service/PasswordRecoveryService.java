package com.ustore.teammanagement.core.service;

import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.entity.PasswordRecoveryToken;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.PasswordRecovery;
import com.ustore.teammanagement.payload.dto.request.ForgotPasswordRequest;
import com.ustore.teammanagement.payload.dto.request.ResetPasswordRequest;
import com.ustore.teammanagement.payload.dto.response.RecoveryTokenResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordRecoveryService {
    private final MemberRepository memberRepository;
    private final PasswordRecovery tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordRecoveryService(MemberRepository memberRepository,
                                   PasswordRecovery tokenRepository,
                                   PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public RecoveryTokenResponse generateRecoveryToken(ForgotPasswordRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail informado."));

        String token = generateSecureToken();

        PasswordRecoveryToken recoveryToken = new PasswordRecoveryToken();
        recoveryToken.setToken(token);
        recoveryToken.setMember(member);
        recoveryToken.setExpiration(LocalDateTime.now().plusMinutes(30)); // mais seguro
        recoveryToken.setUsed(false);

        tokenRepository.save(recoveryToken);

        return new RecoveryTokenResponse(token);
    }

    public void resetPassword(ResetPasswordRequest request) {
        PasswordRecoveryToken tokenEntity = tokenRepository.findByTokenAndUsedFalse(request.token())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado."));

        if (tokenEntity.getExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado.");
        }

        Member member = tokenEntity.getMember();
        member.setPassword(passwordEncoder.encode(request.newPassword()));
        memberRepository.save(member);

        tokenEntity.setUsed(true);
        tokenRepository.save(tokenEntity);
    }
}

