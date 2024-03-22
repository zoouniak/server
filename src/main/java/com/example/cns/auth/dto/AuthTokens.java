package com.example.cns.auth.dto;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {

}
