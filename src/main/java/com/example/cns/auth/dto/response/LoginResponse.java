package com.example.cns.auth.dto.response;

public record LoginResponse(
        AuthTokens authTokens,
        Long memberId
) {
}
