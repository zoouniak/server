package com.example.cns.chat.presentation;

import com.example.cns.chat.converter.MultipartFileConverter;
import com.example.cns.chat.dto.request.FileMessageFormat;
import com.example.cns.chat.dto.request.TextMessageFormat;
import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.chat.service.ChatService;
import com.example.cns.chat.service.MessagePublisher;
import com.example.cns.chat.service.MessageSubscriber;
import com.example.cns.common.service.S3Service;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.Base64;

@RequiredArgsConstructor
@Controller
public class RealTimeChatController {
    public final ChatService chatService;
    public final MemberService memberService;
    private final MessagePublisher messagePublisher;
    private final MessageSubscriber messageSubscriber;
    private final S3Service fileUploader;


    @MessageMapping("/chat-room/{roomId}")
    @SendTo("/sub/chat-room/{roomId}")
    public void sendTextMessage(@DestinationVariable Long roomId, @Payload @Valid TextMessageFormat textMessage) {
        // 데이터베이스에 채팅 저장
        Long save = chatService.saveTextMessage(textMessage);
        String senderProfile = memberService.getMemberProfileUrl(textMessage.memberId());
        //  publisher.publishTextMessage(roomId, textMessage);
        // 채팅 전송
        messagePublisher.publishMessage(roomId, ChatResponse.builder()
                .chatId(save)
                .content(textMessage.content())
                .from(textMessage.from())
                .memberId(textMessage.memberId())
                .profile(senderProfile)
                .createdAt(textMessage.createdAt())
                .messageType(textMessage.messageType())
                .build());
    }

    @MessageMapping("/chat-room/file/{roomId}")
    @SendTo("/sub/chat-room/{roomId}")
    public void sendFileMessage(@DestinationVariable Long roomId, @Payload @Valid FileMessageFormat imageMessage) {
        //  publisher.publishImageMessage(roomId, imageMessage);

        // 디코딩
        byte[] imgByte = Base64.getDecoder().decode(imageMessage.content());

        FileResponse FileResponse = fileUploader.uploadFile(MultipartFileConverter.builder()
                        .imgBytes(imgByte)
                        .originalFileName(imageMessage.originalFileName())
                        .extension(imageMessage.extension())
                        .build(),
                "chat");

        Long save = chatService.saveFileMessage(imageMessage, FileResponse);
        String senderProfileUrl = memberService.getMemberProfileUrl(imageMessage.memberId());

        messagePublisher.publishMessage(roomId, ChatResponse.builder()
                .chatId(save)
                .content(FileResponse.uploadFileURL())
                .from(imageMessage.from())
                .memberId(imageMessage.memberId())
                .profile(senderProfileUrl)
                .createdAt(imageMessage.createdAt())
                .messageType(imageMessage.messageType())
                .build());
    }

    @SubscribeMapping("/chat-room/{roomId}")
    public void subscribeRoom(@DestinationVariable Long roomId) {
        messageSubscriber.subscribe(String.valueOf(roomId));
    }

}
