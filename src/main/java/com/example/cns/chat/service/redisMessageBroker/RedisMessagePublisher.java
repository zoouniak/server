package com.example.cns.chat.service.redisMessageBroker;

import com.example.cns.chat.dto.request.MessageFormat;
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
    public void publish(Long roomId, MessageFormat message) {
        try {
            messageOp.convertAndSend("/sub/chat-room/" + roomId, mapper.writeValueAsString(message));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
