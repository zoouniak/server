package com.example.cns.member.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MemberProfileResponse(
        Long id,
        String nickname,
        String introduction,
        String position,
        String profile,
        String companyName,
        LocalDate birth
) {
}
