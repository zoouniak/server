package com.example.cns.chat.dto.request;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.type.MessageType;
import com.example.cns.member.domain.Member;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TextMessageFormat(
        @NotNull
        String content,

        @NotNull
        Long roomId,

        @NotNull
        LocalDateTime createdAt,

        @NotNull
        Long memberId,

        @NotNull
        String from,

        @NotNull
        MessageType messageType,

        Long subjectId
) {
    public Chat toChatEntity(ChatRoom room, Member from) {
        return Chat.builder()
                .chatRoom(room)
                .from(from)
                .content(this.content)
                .createdAt(createdAt)
                .messageType(messageType)
                .subjectId(this.subjectId)
                .build();
    }

}
