package com.example.cns.chat.presentation;

import com.example.cns.chat.converter.MultipartFileConverter;
import com.example.cns.chat.dto.request.ImageMessageFormat;
import com.example.cns.chat.dto.request.TextMessageFormat;
import com.example.cns.chat.service.ChatService;
import com.example.cns.chat.service.MessagePublisher;
import com.example.cns.chat.service.MessageSubscriber;
import com.example.cns.common.service.S3Service;
import com.example.cns.feed.post.dto.response.FileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.Base64;

@RequiredArgsConstructor
@Controller
public class RealTimeChatController {
    public final ChatService chatService;
    private final MessagePublisher publisher;
    private final MessageSubscriber messageSubscriber;
    private final S3Service fileUploader;


    @MessageMapping("/chat-room/{roomId}")
    @SendTo("/sub/chat-room/{roomId}")
    public void sendTextMessage(@DestinationVariable Long roomId, TextMessageFormat textMessage) {
        // 채팅 전송
        publisher.publishTextMessage(roomId, textMessage);

        // 데이터베이스에 채팅 저장
        chatService.saveTextMessage(textMessage);
    }

    @MessageMapping("/chat-room/image/{roomId}")
    @SendTo("/sub/chat-room/{roomId}")
    public void sendFileMessage(@DestinationVariable Long roomId, ImageMessageFormat imageMessage) {
        publisher.publishImageMessage(roomId, imageMessage);

        // 디코딩
        byte[] imgByte = Base64.getDecoder().decode(imageMessage.content());

        FileResponse FileResponse = fileUploader.uploadFile(new MultipartFileConverter(imgByte), "resume");

        chatService.saveImageMessage(imageMessage, FileResponse);
    }

    @SubscribeMapping("/chat-room/{roomId}")
    public void subscribeRoom(@DestinationVariable Long roomId) {
        messageSubscriber.subscribe(String.valueOf(roomId));
    }
}
