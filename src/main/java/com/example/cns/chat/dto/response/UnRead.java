package com.example.cns.chat.dto.response;

public record UnRead(
        Long chatId,
        int count
) {
}
