package com.example.cns.notification.presentation;

import com.example.cns.auth.config.Auth;
import com.example.cns.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("")
    public ResponseEntity<SseEmitter> connect(@Auth Long memberId) {
        SseEmitter connect = notificationService.connect(memberId);
        try {
            connect.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(connect);
    }
   /* @GetMapping("/event")
    public ResponseEntity sendEvent(){
        Optional<Member> member = memberRepository.findById(1L);
        eventPublisher.publishEvent(new PostLikeEvent(
                member.get(),
                "hi",
                1L
        ));
        return ResponseEntity.ok().build();
    };*/

    @GetMapping("/all")
    public void getNotificationByCursor(@Auth Long memberId, Long cursor) {
        notificationService.getNotifications(memberId, cursor);
    }
}
