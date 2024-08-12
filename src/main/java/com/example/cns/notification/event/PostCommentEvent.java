package com.example.cns.notification.event;

import com.example.cns.member.domain.Member;

public record PostCommentEvent(
        Member to,
        String from,
        Long postId,
        String comment) {
}
