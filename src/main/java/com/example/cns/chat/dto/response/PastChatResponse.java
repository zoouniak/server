package com.example.cns.chat.dto.response;

public record PastChatResponse(
        Long memberId,
        Long lastChat
) {
}
