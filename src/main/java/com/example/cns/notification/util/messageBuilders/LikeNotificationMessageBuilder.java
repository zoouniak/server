package com.example.cns.notification.util.messageBuilders;

import com.example.cns.notification.type.NotificationType;
import com.example.cns.notification.util.NotificationMessageBuilder;
import org.springframework.stereotype.Component;

import static com.example.cns.notification.type.NotificationType.POST_LIKE;

@Component
public class LikeNotificationMessageBuilder implements NotificationMessageBuilder {
    @Override
    public String generateMessage(String from, String content) {
        return from + "님이 내 게시물을 좋아합니다.";
    }

    @Override
    public NotificationType getNotificationType() {
        return POST_LIKE;
    }
}
