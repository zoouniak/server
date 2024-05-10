package com.example.cns.chat.dto.response;

import com.example.cns.auth.dto.response.AuthTokens;

public record LoginResponse(
        AuthTokens authTokens,
        Long memberId
) {
}
