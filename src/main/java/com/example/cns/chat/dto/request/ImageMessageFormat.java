package com.example.cns.chat.dto.request;

import lombok.NonNull;

public record ImageMessageFormat(
        @NonNull
        String content,

        @NonNull
        String originalFileName,

        @NonNull
        String extension,

        @NonNull
        String messageType,

        @NonNull
        Long roomId,

        @NonNull
        Long memberId
) {
}
