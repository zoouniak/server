package com.example.cns.chat.presentation;

import com.example.cns.chat.converter.MultipartFileConverter;
import com.example.cns.chat.dto.request.MessageFormat;
import com.example.cns.chat.service.ChatService;
import com.example.cns.chat.service.MessagePublisher;
import com.example.cns.chat.service.MessageSubscriber;
import com.example.cns.common.service.S3Service;
import com.example.cns.feed.post.dto.response.PostFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class RealTimeChatController {
    public final ChatService chatService;
    private final MessagePublisher publisher;
    private final MessageSubscriber messageSubscriber;
    private final S3Service fileUploader;


    @MessageMapping("/chat-room/{roomId}")
    @SendTo("/sub/chat-room/{roomId}")
    public void sendTextMessage(@DestinationVariable Long roomId, MessageFormat textMessage) {
        // 데이터베이스에 채팅 저장
        chatService.saveTextMessage(textMessage);
        // 채팅 전송
        publisher.publish(roomId, textMessage);
    }

    @MessageMapping("/chat-room/image/{roomId}")
    @SendTo("/sub/chat-room/{roomId}")
    public void sendFileMessage(@DestinationVariable Long roomId, MessageFormat imageMessage) {
        // 디코딩
        byte[] imgByte = Base64.getDecoder().decode(imageMessage.content());

        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(new MultipartFileConverter(imgByte));
        List<PostFileResponse> postFileResponses = fileUploader.uploadPostFile(fileList, "resume");

        chatService.saveImageMessage(imageMessage, postFileResponses);

        publisher.publish(roomId, imageMessage);
    }

    @SubscribeMapping("/chat-room/{roomId}")
    public void subscribeRoom(@DestinationVariable Long roomId) {
        messageSubscriber.subscribe(String.valueOf(roomId));
    }
}
