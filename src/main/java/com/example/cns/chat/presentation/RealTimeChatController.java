package com.example.cns.chat.presentation;

import com.example.cns.chat.dto.MessageFormat;
import com.example.cns.chat.service.ChatService;
import com.example.cns.chat.service.MessagePublisher;
import com.example.cns.chat.service.MessageSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class RealTimeChatController {
    public final ChatService chatService;
    private final MessagePublisher publisher;
    private final MessageSubscriber messageSubscriber;

    @MessageMapping("/{roomId}")
    @SendTo("/sub/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, MessageFormat message) {
        // 데이터베이스에 채팅 저장
        /*
         * 1. 사용자 정보를 어디에 담아 보낼지 (jwt or messageFormat에 memberId)
         * 2. 채팅방번호-사용자 (chatParticipation)을 저장해야하는데
         *  2-1. 채팅방 생성 시 저장
         *  2-2. (추가 초대될 경우) 어떻게 처리하지???
         *       추가 초대 api를 만들고 그때 저장?
         * 3.
         */
        chatService.saveMessage(message);
        // 채팅 전송
        publisher.publish(roomId, message);
    }

    @SubscribeMapping("/chat/{roomId}")
    public void subscribeRoom(@DestinationVariable Long roomId) {
        messageSubscriber.subscribe(String.valueOf(roomId));
    }
}
