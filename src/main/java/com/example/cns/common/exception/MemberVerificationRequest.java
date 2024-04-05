package com.example.cns.common.exception;

public record MemberVerificationRequest(
        String firstName,
        String lastName,
        String nickname,
        String email
) {
}
