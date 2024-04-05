package com.example.cns.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;

@Schema(description = "비밀번호 초기화 요청 dto")
public record PasswordResetRequest(
        @NotBlank @Schema(description = "성")
        String firstName,
        @NotBlank @Schema(description = "이름")
        String lastName,
        @NotBlank @Schema(description = "아이디")
        String nickname,
        @NotBlank @Schema(description = "이메일")
        String email,

        @NotBlank @Schema(description = "새로운 비밀번호")
        String password

) {
    public String toEncodePassword(PasswordEncoder passwordEncoder) {
        return passwordEncoder.encode(password);
    }
}
