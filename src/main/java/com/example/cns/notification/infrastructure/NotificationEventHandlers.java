package com.example.cns.notification.infrastructure;

import com.example.cns.common.exception.NotificationException;
import com.example.cns.notification.event.NotificationEvent;
import com.example.cns.notification.type.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.cns.common.exception.ExceptionCode.NOT_SUPPORT_ALARM;

@Component
public class NotificationEventHandlers {
    private final List<NotificationHandler<? extends NotificationEvent>> handlers;

    public NotificationEventHandlers(List<NotificationHandler<? extends NotificationEvent>> handlers) {
        this.handlers = handlers;
    }

    public NotificationHandler<? extends NotificationEvent> mapping(NotificationType type) {
        return handlers.stream()
                .filter(handler -> handler.is(type))
                .findFirst()
                .orElseThrow(() -> new NotificationException(NOT_SUPPORT_ALARM));
    }
}
