package com.example.cns.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 회사/직무 수정 요청 DTO")
public record MemberCompanyPatchRequest(
        @Schema(description = "회사 이름")
        String companyName,
        @Schema(description = "직무 이름")
        String position
) {
}
