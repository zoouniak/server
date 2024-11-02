package com.example.cns.chat.dto.request;

import com.example.cns.chat.type.MessageType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record FileMessageFormat(
        @NotNull
        String content,

        @NotNull
        String originalFileName,

        @NotNull
        String extension,

        @NotNull
        MessageType messageType,

        @NotNull
        LocalDateTime createdAt,

        @NotNull
        Long roomId,

        @NotNull
        Long memberId,

        @NotNull
        String from
) {
}
