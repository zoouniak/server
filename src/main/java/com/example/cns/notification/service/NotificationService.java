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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final CustomNotificationRepository customNotificationRepository;
    private final Map<Long, SseEmitter> emitters;

    private final MessageBuilderFactory messageBuilderFactory;

    public NotificationService(NotificationRepository notificationRepository, CustomNotificationRepository customNotificationRepository, MessageBuilderFactory messageBuilderFactory) {
        this.notificationRepository = notificationRepository;
        this.customNotificationRepository = customNotificationRepository;
        this.emitters = new ConcurrentHashMap<>();
        this.messageBuilderFactory = messageBuilderFactory;
    }

    public List<NotificationResponse> getNotifications(final Long memberId, final Long cursor) {
        return customNotificationRepository.getNotificationsByCursor(memberId, cursor);
    }

    public SseEmitter connect(final Long userId) {
        SseEmitter emitter = new SseEmitter();
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> {
            log.info("sse 연결 종료 : userId = " + userId);
            emitters.remove(userId);
        });
        emitter.onTimeout(() -> {

        });

        return emitter;
    }
    @EventListener
    public void sendCommentNotification(final PostCommentEvent event) {
        NotificationMessageBuilder builder = messageBuilderFactory.getBuilder(POST_COMMENT);
        String message = builder.generateMessage(event.from().getNickname(), event.comment());

        Notification notification = notificationRepository.save(Notification.builder()
                .from(event.from())
                .to(event.to())
                .subjectId(event.postId())
                .message(message)
                .notificationType(POST_COMMENT)
                .createdAt(LocalDateTime.now())
                .build());
        sendNotification(event.to().getId(), NotificationResponse.of(notification, event.from()));

    }

    @EventListener
    public void sendPostLikeNotification(final PostLikeEvent event) {
        NotificationMessageBuilder builder = messageBuilderFactory.getBuilder(NotificationType.POST_LIKE);
        String message = builder.generateMessage(event.from().getNickname(), null);


        Notification notification = notificationRepository.save(Notification.builder()
                .to(event.to())
                .subjectId(event.postId())
                .message(message)
                .notificationType(POST_LIKE)
                .createdAt(LocalDateTime.now())
                .build());
        sendNotification(event.to().getId(), NotificationResponse.of(notification, event.from()));
    }

    private void sendNotification(final Long to, final NotificationResponse notificationResponse) {
        Optional.ofNullable(emitters.get(to))
                .ifPresent(emitter -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .data(notificationResponse));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


}
