package com.example.cns.member.dto.response;

import lombok.Builder;

@Builder
public record MemberSearchResponse(
        Long memberId,
        String nickname
) {
}
