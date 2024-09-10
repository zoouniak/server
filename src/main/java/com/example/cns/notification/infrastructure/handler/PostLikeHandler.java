package com.example.cns.notification.infrastructure.handler;

import com.example.cns.notification.domain.Notification;
import com.example.cns.notification.domain.repository.NotificationRepository;
import com.example.cns.notification.dto.response.NotificationResponse;
import com.example.cns.notification.event.PostLikeEvent;
import com.example.cns.notification.infrastructure.NotificationHandler;
import com.example.cns.notification.infrastructure.NotificationSender;
import com.example.cns.notification.type.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.cns.notification.type.NotificationType.POST_COMMENT;

@Service
@RequiredArgsConstructor
public class PostLikeHandler implements NotificationHandler<PostLikeEvent> {
    private final NotificationRepository notificationRepository;

    @Override
    public void handle(PostLikeEvent event, NotificationSender sender) {
        Notification notification = notificationRepository.save(Notification.builder()
                .from(event.from())
                .to(event.to())
                .subjectId(event.postId())
                .message(generateMessage(event))
                .notificationType(POST_COMMENT)
                .createdAt(LocalDateTime.now())
                .build());
        sender.sendNotification(event.to().getId(), NotificationResponse.of(notification, event.from()));
    }

    @Override
    public String generateMessage(PostLikeEvent event) {
        return event.from().getNickname() + "님이 내 게시물을 좋아합니다.";
    }

    @Override
    public boolean is(NotificationType type) {
        return type.equals(NotificationType.POST_LIKE);
    }
}
