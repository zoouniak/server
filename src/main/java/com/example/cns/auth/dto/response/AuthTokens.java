package com.example.cns.auth.dto.response;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {

}
