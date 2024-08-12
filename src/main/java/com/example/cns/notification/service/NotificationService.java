package com.example.cns.notification.service;

import com.example.cns.notification.domain.Notification;
import com.example.cns.notification.domain.repository.CustomNotificationRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.cns.notification.type.NotificationType.POST_COMMENT;
import static com.example.cns.notification.type.NotificationType.POST_LIKE;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final CustomNotificationRepository customNotificationRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final MessageBuilderFactory messageBuilderFactory;

    public NotificationService(NotificationRepository notificationRepository, CustomNotificationRepository customNotificationRepository, MessageBuilderFactory messageBuilderFactory) {
        this.notificationRepository = notificationRepository;
        this.customNotificationRepository = customNotificationRepository;
        this.messageBuilderFactory = messageBuilderFactory;
    }

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter();
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    private Long saveNotification(final Notification notification) {
        Notification save = notificationRepository.save(notification);
        return save.getId();
    }

    @EventListener
    public void sendCommentNotification(final PostCommentEvent event) {
        NotificationMessageBuilder builder = messageBuilderFactory.getBuilder(POST_COMMENT);
        String message = builder.generateMessage(event.from(), event.comment());

        Long notificationId = saveNotification(Notification.builder()
                .to(event.to())
                .subjectId(event.postId())
                .message(message)
                .notificationType(POST_COMMENT)
                .createdAt(LocalDateTime.now())
                .build());
        sendNotification(notificationId, event.to().getId(), message, event.postId(), POST_COMMENT);

    }

    @EventListener
    public void sendPostLikeNotification(final PostLikeEvent event) {
        NotificationMessageBuilder builder = messageBuilderFactory.getBuilder(NotificationType.POST_LIKE);
        String message = builder.generateMessage(event.from(), null);

        Long notificationId = saveNotification(Notification.builder()
                .to(event.to())
                .subjectId(event.postId())
                .message(message)
                .notificationType(POST_LIKE)
                .createdAt(LocalDateTime.now())
                .build());
        sendNotification(notificationId, event.to().getId(), message, event.postId(), NotificationType.POST_LIKE);
    }

    private void sendNotification(final Long nId, final Long userId, final String message, final Long subjectId, final NotificationType type) {
        Optional.ofNullable(emitters.get(userId))
                .ifPresent(emitter -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .data(new NotificationResponse(
                                        nId,
                                        message,
                                        subjectId,
                                        type
                                )));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public List<NotificationResponse> getNotifications(final Long memberId, final Long cursor) {
        return customNotificationRepository.getNotificationsByCursor(memberId, cursor);
    }
}
