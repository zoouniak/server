package com.example.cns.chat.service.redisMessageBroker;

import com.example.cns.chat.dto.request.FileMessageFormat;
import com.example.cns.chat.dto.request.TextMessageFormat;
import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.chat.service.MessagePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {
    private final SimpMessageSendingOperations messageOp;
    private final ObjectMapper mapper;

    @Override
    public void publishTextMessage(Long roomId, TextMessageFormat message) {
        try {
            messageOp.convertAndSend("/sub/chat-room/" + roomId, mapper.writeValueAsString(message));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void publishImageMessage(Long roomId, FileMessageFormat fileMessageFormat) {
        try {
            messageOp.convertAndSend("/sub/chat-room/" + roomId, mapper.writeValueAsString(fileMessageFormat));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void publishMessage(Long roomId, ChatResponse chat) {
        try {
            messageOp.convertAndSend("/sub/chat-room/" + roomId, mapper.writeValueAsString(chat));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
