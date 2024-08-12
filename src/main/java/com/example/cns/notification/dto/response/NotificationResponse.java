package com.example.cns.notification.dto.response;

import com.example.cns.notification.type.NotificationType;

public record NotificationResponse(
        String message,
        Long subjectId,
        NotificationType type
) {
}
