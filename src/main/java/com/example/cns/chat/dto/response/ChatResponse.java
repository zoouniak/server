package com.example.cns.chat.dto.response;

import com.example.cns.chat.type.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatResponse(
        Long chatId,

        String content,

        String from,

        Long memberId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt,

        MessageType messageType
) {
}
