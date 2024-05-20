package com.example.cns.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "회원 정보 응답 DTO")
@Builder
public record MemberProfileResponse(
        @Schema(description = "회원 인덱스")
        Long id,
        @Schema(description = "회원 아이디")
        String nickname,
        @Schema(description = "회원 소개")
        String introduction,
        @Schema(description = "회원 직무")
        String position,
        @Schema(description = "회원 프로필 이미지")
        String profile,
        @Schema(description = "회원 회사 이름")
        String companyName,
        @Schema(description = "회원 생년월일")
        LocalDate birth
) {
}
