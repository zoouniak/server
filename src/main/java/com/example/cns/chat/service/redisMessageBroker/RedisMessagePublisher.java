package com.example.cns.chat.service.redisMessageBroker;

import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.chat.service.MessagePublisher;
import com.example.cns.common.exception.ChatException;
import com.example.cns.common.exception.ExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public void publishMessage(Long roomId, ChatResponse chat) {

        try {
            messageOp.convertAndSend("/sub/chat-room/" + roomId, mapper.writeValueAsString(chat));
        } catch (JsonProcessingException e) {
            throw new ChatException(ExceptionCode.FAIL_SEND_EMAIL);
        }

    }


}
