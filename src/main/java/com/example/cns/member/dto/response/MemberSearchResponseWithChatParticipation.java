package com.example.cns.member.dto.response;

public record MemberSearchResponseWithChatParticipation(
        Long memberId,
        String nickname,
        boolean isParticipate
) {
}
