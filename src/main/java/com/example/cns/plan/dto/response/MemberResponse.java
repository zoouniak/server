package com.example.cns.plan.dto.response;

public record MemberResponse(
        Long memberId,
        String nickname,
        String profile
) {
}
