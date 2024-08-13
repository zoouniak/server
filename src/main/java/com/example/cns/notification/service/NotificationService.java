package com.example.cns.notification.service;

import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.exception.NotificationException;
import com.example.cns.notification.domain.repository.CustomNotificationRepository;
import com.example.cns.notification.dto.response.NotificationResponse;
import com.example.cns.notification.event.NotificationEvent;
import com.example.cns.notification.infrastructure.NotificationEventHandlers;
import com.example.cns.notification.infrastructure.NotificationHandler;
import com.example.cns.notification.infrastructure.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class NotificationService {

    private final CustomNotificationRepository customNotificationRepository;
    private final NotificationSender notificationSender;
    private final NotificationEventHandlers handlers;

    public NotificationService(CustomNotificationRepository customNotificationRepository, NotificationSender notificationSender, NotificationEventHandlers handlers) {
        this.customNotificationRepository = customNotificationRepository;
        this.notificationSender = notificationSender;
        this.handlers = handlers;
    }

    public List<NotificationResponse> getNotifications(final Long memberId, final Long cursor) {
        return customNotificationRepository.getNotificationsByCursor(memberId, cursor);
    }

    public SseEmitter connect(final Long memberId) {
        SseEmitter emitter = notificationSender.connect(memberId);
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            throw new NotificationException(ExceptionCode.FAIL_WITH_SSE);
        }
        return emitter;
    }

    @EventListener
    public void handleNotificationEvent(NotificationEvent notificationEvent) {
        NotificationHandler handler = handlers.mapping(notificationEvent.getNotificationTye());
        handler.handle(notificationEvent, notificationSender);
    }
}
