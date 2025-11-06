package com.ustore.teammanagement.core.repository;
import com.ustore.teammanagement.payload.dto.request.LoginRequest;

public interface AuthenticationService {
    String generateAccessToken(LoginRequest request);
    String refreshAccessToken(String refreshToken);
}
