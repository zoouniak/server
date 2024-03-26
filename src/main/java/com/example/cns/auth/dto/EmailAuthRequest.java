package com.example.cns.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증 확인 dto")
public record EmailAuthRequest(
        @NotBlank @Schema(description = "이메일")
        String email,
        @NotBlank @Schema(description = "입력받은 인증번호")
        String authCode
) {
}
