package com.ustore.teammanagement.core.service;


import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.entity.PasswordResetToken;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.core.repository.PasswordResetTokenRepository;
import com.ustore.teammanagement.exception.ResourceNotFoundException;
import com.ustore.teammanagement.exception.UnauthorizedException;
import com.ustore.teammanagement.payload.dto.request.LoginRequest;
import com.ustore.teammanagement.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final MemberService memberService;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);


    public String authenticationLogin(LoginRequest dto) {
        Member user = memberRepository.findByEmail(dto.email())
                .orElseThrow(() ->
                     new UnauthorizedException("Usuário ou senha inválidos.")
                );

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            LocalDateTime unlockTime = user.getLockTime().plusMinutes(15);

            if (LocalDateTime.now().isBefore(unlockTime)) {
                long minutesRemaining = ChronoUnit.MINUTES.between(LocalDateTime.now(), unlockTime);
                throw new UnauthorizedException(
                        "Conta bloqueada devido a múltiplas tentativas malsucedidas. " +
                                "Tente novamente em " + minutesRemaining + " minutos."
                );
            } else {
                user.setAccountLocked(false);
                user.setFailedAttempts(0);
                user.setLockTime(null);
                memberRepository.save(user);
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
            );

            user.setFailedAttempts(0);
            user.setAccountLocked(false);
            user.setLockTime(null);
            memberRepository.save(user);

            return jwtUtil.generateToken(authentication.getName());

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            if (attempts >= 3) {
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
                memberRepository.save(user);

                logger.error("Conta bloqueada para {} após {} tentativas.", dto.email(), attempts);
                throw new UnauthorizedException(
                        "Sua conta foi bloqueada após 3 tentativas inválidas. Tente novamente em 15 minutos."
                );
            } else {
                memberRepository.save(user);
                throw new UnauthorizedException("Usuário ou senha inválidos. Tentativa " + attempts + " de 3.");
            }
        }
    }

    public String passwordRecovery(String email) {
        var member = memberRepository.findByEmail(email)
                .orElseThrow(() ->
                     new ResourceNotFoundException("Usuário não encontrado")
                );

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setMember(member);
        resetToken.setExpiryDate(expiry);

        tokenRepository.save(resetToken);

        return token;
    }

    public void resetPassword(String token, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("As senhas não coincidem");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.error("Token de redefinição inválido: {}", token);
                    return new ResourceNotFoundException("Token inválido");
                });

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado");
        }

        var member = resetToken.getMember();
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        tokenRepository.delete(resetToken);
    }
}