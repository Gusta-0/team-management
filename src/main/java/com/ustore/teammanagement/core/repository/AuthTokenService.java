package com.ustore.teammanagement.core.repository;

public interface AuthTokenService {
    String refreshAccessToken(String token);
}
