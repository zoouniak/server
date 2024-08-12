package com.example.cns.notification.util;

import com.example.cns.notification.type.NotificationType;

public interface NotificationMessageBuilder {
    String generateMessage(String from, String content);

    NotificationType getNotificationType();
}
