package com.example.cns.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MemberVerificationRequest(
        @NotBlank @Schema(description = "성")
        String firstName,
        @NotBlank @Schema(description = "이름")
        String lastName,
        @NotBlank @Schema(description = "아이디")
        String nickname,
        @NotBlank @Schema(description = "이메일")
        String email
) {
}
