package com.ustore.teammanagement.payload.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        int expiresIn
) {
}
