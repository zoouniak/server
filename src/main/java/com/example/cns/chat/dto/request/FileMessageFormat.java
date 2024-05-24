package com.example.cns.chat.dto.request;

import com.example.cns.chat.type.MessageType;
import lombok.NonNull;

import java.time.LocalDateTime;

public record FileMessageFormat(
        @NonNull
        String content,

        @NonNull
        String originalFileName,

        @NonNull
        String extension,

        @NonNull
        MessageType messageType,

        @NonNull
        LocalDateTime createdAt,

        @NonNull
        Long roomId,

        @NonNull
        Long memberId,

        @NonNull
        String from
) {
}
