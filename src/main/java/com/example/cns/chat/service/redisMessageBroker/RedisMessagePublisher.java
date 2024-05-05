package com.example.cns.chat.service.redisMessageBroker;

import com.example.cns.chat.dto.MessageFormat;
import com.example.cns.chat.service.MessagePublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations messageOp;
    private final ObjectMapper mapper;

    @Override
    public void publish(Long roomId, MessageFormat message) {
        try {
            messageOp.convertAndSend("/sub/chat/" + roomId, mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
    }

}
