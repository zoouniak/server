package com.example.cns.chat.handler;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

/*
 * 실시간 채팅 컨트롤러에서 발생하는 business exception 처리
 * 로깅 및 사용자에게 에러 알림
 */
@ControllerAdvice
@Slf4j
public class ChatExceptionHandler {

    private final SimpMessagingTemplate template;

    public ChatExceptionHandler(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(MethodArgumentNotValidException exception) {
        log.error("채팅 전송 실패" + exception.getMessage());
    }

    @MessageExceptionHandler(BusinessException.class)
    public void handleBusinessExceptionInText(@DestinationVariable Long roomId, BusinessException exception) {
        ExceptionResponse response = new ExceptionResponse(exception.getErrorCode(), exception.getMessage());
        log.error("실시간 채팅 중 에러 발생 >> 에러코드 [" + exception.getErrorCode() + "] " + exception.getMessage());
        template.convertAndSend("/sub/chat-room/" + roomId, response, Map.of("messageType", "error"));
    }

}
