package com.example.cns.auth.dto;

import com.example.cns.member.domain.Member;
import com.example.cns.member.type.RoleType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;

public record SignUpReq(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        String username,
        @NotBlank
        String password,
        @NotBlank
        String email,
        String position,
        @NotBlank
        String companyName,
        @NotBlank
        Date birth
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