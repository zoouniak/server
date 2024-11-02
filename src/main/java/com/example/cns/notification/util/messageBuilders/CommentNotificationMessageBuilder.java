package com.example.cns.notification.util.messageBuilders;

import com.example.cns.notification.type.NotificationType;
import com.example.cns.notification.util.NotificationMessageBuilder;
import org.springframework.stereotype.Component;

import static com.example.cns.notification.type.NotificationType.POST_COMMENT;

@Component
public class CommentNotificationMessageBuilder implements NotificationMessageBuilder {
    @Override
    public String generateMessage(String from, String content) {
        return from + "님이 내 게시물에 댓글을 남겼습니다 : " + content;
    }

    @Override
    public NotificationType getNotificationType() {
        return POST_COMMENT;
    }
}
