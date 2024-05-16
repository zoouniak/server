package com.example.cns.member.dto.request;

public record MemberCompanyPatchRequest(
        String companyName,
        String position
) {
}
