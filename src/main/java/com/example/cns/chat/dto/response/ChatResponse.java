package com.example.cns.chat.dto.response;

import com.example.cns.chat.type.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ChatResponse(
        Long chatId,
        String content,
        String from,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,
        MessageType messageType
) {
}
