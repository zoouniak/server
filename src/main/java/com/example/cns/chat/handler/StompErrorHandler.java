package com.example.cns.chat.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class StompErrorHandler extends StompSubProtocolErrorHandler {
    /*
     * 1. Jwt 토큰 없을 시 해당 handler에서 처리
     * 2. 메시지 처리 중 발생하는 에러 handler
     */
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);
        log.error(ex.getMessage());
        // BusinessException exception = (BusinessException) ex.getCause();
        return MessageBuilder.createMessage(ex.getMessage().getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders());
    }
}