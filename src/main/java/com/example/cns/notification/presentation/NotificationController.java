package com.example.cns.notification.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.notification.dto.response.NotificationResponse;
import com.example.cns.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<SseEmitter> connect(@Auth final Long memberId) {
        return ResponseEntity.ok(notificationService.connect(memberId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getNotificationByCursor(@Auth final Long memberId, @RequestParam(name = "cursor", required = false) final Long cursor) {
        return ResponseEntity.ok(notificationService.getNotifications(memberId, cursor));
    }
}
