package com.example.cns.notification.event;

import com.example.cns.member.domain.Member;
import com.example.cns.notification.type.NotificationType;

public record PostCommentEvent(
        Member to,
        Member from,
        Long postId,
        String comment) implements NotificationEvent {
    @Override
    public NotificationType getNotificationTye() {
        return NotificationType.POST_COMMENT;
    }
}
