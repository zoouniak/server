package com.example.cns.notification.infrastructure.handler;

import com.example.cns.notification.domain.Notification;
import com.example.cns.notification.domain.repository.NotificationRepository;
import com.example.cns.notification.dto.response.NotificationResponse;
import com.example.cns.notification.event.PostCommentEvent;
import com.example.cns.notification.infrastructure.NotificationHandler;
import com.example.cns.notification.infrastructure.NotificationSender;
import com.example.cns.notification.type.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.cns.notification.type.NotificationType.POST_COMMENT;

@Service
@RequiredArgsConstructor
public class PostCommentHandler implements NotificationHandler<PostCommentEvent> {
    private final NotificationRepository notificationRepository;


    @Override
    public void handle(PostCommentEvent event, NotificationSender sender) {
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
    public String generateMessage(PostCommentEvent event) {
        return event.from().getNickname() + "님이 내 게시물에 댓글을 남겼습니다. : " + event.comment();
    }

    @Override
    public boolean is(NotificationType type) {
        return type.equals(POST_COMMENT);
    }
}
