package com.example.cns.notification.infrastructure;

import com.example.cns.notification.event.NotificationEvent;
import com.example.cns.notification.type.NotificationType;

public interface NotificationHandler<T extends NotificationEvent> {
    void handle(T event, NotificationSender sender);

    String generateMessage(T event);

    boolean is(NotificationType type);
}
