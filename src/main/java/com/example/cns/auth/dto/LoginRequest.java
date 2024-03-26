package com.example.cns.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 dto")
public record LoginRequest(
        @NotBlank @Schema(description = "아이디")
        String username,

        @NotBlank @Schema(description = "비밀번호")
        String password
) {
}
