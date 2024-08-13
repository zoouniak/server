package com.example.cns.notification.dto.response;

import com.example.cns.feed.post.dto.response.PostMember;
import com.example.cns.member.domain.Member;
import com.example.cns.notification.domain.Notification;
import com.example.cns.notification.type.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@AllArgsConstructor
@Getter
public class NotificationResponse {
    Long notificationId;
    PostMember from;
    String message;
    Long subjectId;
    NotificationType type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    LocalDateTime createdAt;

    public NotificationResponse(Long notificationId, Long fromId, String fromNickname, String fromProfile, String message, Long subjectId, NotificationType type, LocalDateTime createdAt) {
        this(notificationId, new PostMember(fromId, fromNickname, fromProfile), message, subjectId, type, createdAt);
    }

    public static NotificationResponse of(Notification notification, Member from) {
        return new NotificationResponse(
                notification.getId(),
                new PostMember(from.getId(),
                        from.getNickname(),
                        from.getUrl()),
                notification.getMessage(),
                notification.getSubjectId(),
                notification.getNotificationType(),
                LocalDateTime.now()
        );
    }
}
