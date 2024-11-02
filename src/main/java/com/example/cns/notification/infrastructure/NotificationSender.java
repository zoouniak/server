package com.example.cns.notification.infrastructure;

import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.exception.NotificationException;
import com.example.cns.notification.dto.response.NotificationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationSender {
    private final Map<Long, SseEmitter> emitters;

    public NotificationSender() {
        this.emitters = new ConcurrentHashMap<>();
    }

    public SseEmitter connect(final Long userId) {
        SseEmitter emitter = new SseEmitter();
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> {

        });

        return emitter;
    }

    public void sendNotification(final Long to, final NotificationResponse notificationResponse) {
        Optional.ofNullable(emitters.get(to))
                .ifPresent(emitter -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .data(notificationResponse));
                    } catch (IOException e) {
                        throw new NotificationException(ExceptionCode.FAIL_WITH_SSE);
                    }
                });
    }
}
