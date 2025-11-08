package com.ustore.teammanagement.core.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ustore.teammanagement.core.entity.Member;
import com.ustore.teammanagement.core.repository.AuthTokenService;
import com.ustore.teammanagement.core.repository.MemberRepository;
import com.ustore.teammanagement.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService implements AuthTokenService {

    @Value("${JWT_SECRET}")
    private String securityJwt;

    private final MemberRepository memberRepository;

    public TokenService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public String generateToken(Member member){
        return JWT.create().withIssuer(member.getName())
                .withSubject(member.getEmail()).withExpiresAt(dateExpiration(1))
                .withClaim("type", "access")
                .sign(Algorithm.HMAC256(securityJwt));
    }

    public String getSubject(String token) {
        Algorithm algorithm = Algorithm.HMAC256(securityJwt);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("Team Management App")
                .withClaim("type", "access")
                .build();

        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getSubject();
    }


    private Instant dateExpiration(int hours){
        return LocalDateTime.now().plusHours(hours).toInstant(ZoneOffset.of("-03:00"));
    }

    @Override
    public String refreshAccessToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(securityJwt)).withClaim("type", "refresh").build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        String userEmail = decodedJWT.getSubject();

        Member member = this.memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found with e-mail: %s", userEmail)));

        return this.generateToken(member);
    }

    public String generateRefreshToken(Member member){
        return JWT.create().withIssuer("Team Management App")
                .withSubject(member.getEmail()).withExpiresAt(dateExpiration(24*7))
                .withClaim("type", "refresh")
                .sign(Algorithm.HMAC256(securityJwt));
    }
}
