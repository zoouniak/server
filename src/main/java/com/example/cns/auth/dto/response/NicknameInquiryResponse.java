package com.example.cns.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "아이디 찾기 응답 dto")
public record NicknameInquiryResponse(
        @Schema(description = "아이디")
        String nickname
) {
}
