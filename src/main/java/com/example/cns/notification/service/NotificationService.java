package com.example.cns.notification.service;

import com.example.cns.notification.domain.Notification;
import com.example.cns.notification.domain.repository.NotificationRepository;
import com.example.cns.notification.dto.response.NotificationResponse;
import com.example.cns.notification.event.PostCommentEvent;
import com.example.cns.notification.event.PostLikeEvent;
import com.example.cns.notification.type.NotificationType;
import com.example.cns.notification.util.MessageBuilderFactory;
import com.example.cns.notification.util.NotificationMessageBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.cns.notification.type.NotificationType.POST_COMMENT;
import static com.example.cns.notification.type.NotificationType.POST_LIKE;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final MessageBuilderFactory messageBuilderFactory;

    public NotificationService(NotificationRepository notificationRepository, MessageBuilderFactory messageBuilderFactory) {
        this.notificationRepository = notificationRepository;
        this.messageBuilderFactory = messageBuilderFactory;
    }

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter();
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    private void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    @EventListener
    public void sendCommentNotification(PostCommentEvent event) {
        NotificationMessageBuilder builder = messageBuilderFactory.getBuilder(POST_COMMENT);
        String message = builder.generateMessage(event.from(), event.comment());

        sendNotification(event.to().getId(), message, event.postId(), POST_COMMENT);
        saveNotification(Notification.builder()
                .to(event.to())
                .subjectId(event.postId())
                .message(message)
                .notificationType(POST_COMMENT)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @EventListener
    public void sendPostLikeNotification(PostLikeEvent event) {
        NotificationMessageBuilder builder = messageBuilderFactory.getBuilder(NotificationType.POST_LIKE);
        String message = builder.generateMessage(event.from(), null);

        sendNotification(event.to().getId(), message, event.postId(), NotificationType.POST_LIKE);
        saveNotification(Notification.builder()
                .to(event.to())
                .subjectId(event.postId())
                .message(message)
                .notificationType(POST_LIKE)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private void sendNotification(Long userId, String message, Long subjectId, NotificationType type) {
        Optional.ofNullable(emitters.get(userId))
                .ifPresent(emitter -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .data(new NotificationResponse(
                                        message,
                                        subjectId,
                                        type
                                )));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void getNotifications(Long memberId, Long cursor) {

    }
}
