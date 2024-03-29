package com.example.cns.auth.dto.request;

import com.example.cns.member.domain.Member;
import com.example.cns.member.type.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Schema(description = "회원가입 요청 dto")
public record SignUpRequest(
        @NotBlank @Schema(description = "성")
        String firstName,

        @NotBlank @Schema(description = "이름")
        String lastName,

        @NotBlank @Schema(description = "아이디")
        String username,

        @NotBlank @Schema(description = "비밀번호")
        String password,

        @NotBlank @Schema(description = "이메일")
        String email,

        @Schema(description = "직무")
        String position,

        @NotBlank @Schema(description = "재직중인 회사 이름")
        String companyName,

        @NotBlank @Schema(description = "생일")
        LocalDate birth
) {
    public Member toEntity(PasswordEncoder passwordEncoder) {
        RoleType roleType = companyName.equals("없음") ? RoleType.GENERAL : RoleType.EMPLOYEE;

        return Member.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .birth(birth)
                .role(roleType)
                .position(position)
                .build();
    }

}