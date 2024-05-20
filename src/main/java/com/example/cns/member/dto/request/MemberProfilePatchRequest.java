package com.example.cns.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "회원 정보 수정 요청 DTO")
public record MemberProfilePatchRequest(
        @Schema(description = "회원 한줄 소개")
        String introduction,
        @Schema(description = "회원 생년월일")
        LocalDate birth
) {
}
