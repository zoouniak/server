package com.example.cns.chat.config;

import com.example.cns.common.exception.BusinessException;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);
        BusinessException exception = (BusinessException) ex.getCause();
        return MessageBuilder.createMessage(exception.getMessage().getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders());
    }
    // return super.handleClientMessageProcessingError(clientMessage, ex);
}

