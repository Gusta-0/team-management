package com.ustore.teammanagement.core.repository;


import com.ustore.teammanagement.payload.dto.request.LoginRequest;
import com.ustore.teammanagement.payload.dto.request.RefreshToken;
import com.ustore.teammanagement.payload.dto.request.TokenForm;

public interface AuthenticationService {

    String generateAccessToken(LoginRequest request);
//    TokenForm authenticationAndGenerateToken(LoginRequest request);
    String refreshAccessToken(String refreshToken);
}
