package com.example.cns.chat.service;

import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.chat.dto.response.UnRead;
import com.example.cns.common.exception.ChatException;
import com.example.cns.common.exception.ExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimpMessagePublisher implements MessagePublisher {
    private final SimpMessagingTemplate messageOp;
    private final ObjectMapper mapper;

    @Override
    public void publishMessage(Long roomId, ChatResponse chat) {
        try {
            messageOp.convertAndSend("/sub/chat-room/" + roomId, mapper.writeValueAsString(chat));
        } catch (JsonProcessingException e) {
            throw new ChatException(ExceptionCode.FAIL_SEND_INFOMSG);
        }
    }

    @Override
    public void publishUnRead(Long roomId, List<UnRead> unReadList) {
        try {
            messageOp.convertAndSend("/unread/" + roomId, mapper.writeValueAsString(unReadList));
        } catch (JsonProcessingException e) {
            throw new ChatException(ExceptionCode.FAIL_SEND_INFOMSG);
        }
    }
}
