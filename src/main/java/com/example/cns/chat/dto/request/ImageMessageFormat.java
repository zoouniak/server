package com.example.cns.chat.dto.request;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.type.MessageType;
import com.example.cns.member.domain.Member;
import lombok.NonNull;

import java.time.LocalDateTime;

public record ImageMessageFormat(
        @NonNull
        String content,

        @NonNull
        String originalFileName,

        @NonNull
        String extension,

        @NonNull
        Long roomId,

        @NonNull
        Long memberId
) {
    public Chat toChatEntity(ChatRoom chatRoom, Member sender, LocalDateTime now) {
        return Chat.builder()
                .from(sender)
                .messageType(MessageType.IMAGE)
                .createdAt(now)
                .subjectId(null)
                .content("")
                .chatRoom(chatRoom)
                .build();
    }
}
