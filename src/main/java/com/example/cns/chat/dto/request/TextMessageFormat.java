package com.example.cns.chat.dto.request;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.type.MessageType;
import com.example.cns.member.domain.Member;
import lombok.NonNull;

import java.time.LocalDateTime;

public record TextMessageFormat(
        @NonNull
        String content,

        @NonNull
        Long roomId,

        @NonNull
        Long memberId,

        @NonNull
        String messageType,

        Long subjectId
) {
    public Chat toChatEntity(ChatRoom room, Member from, LocalDateTime now) {
        return Chat.builder()
                .chatRoom(room)
                .from(from)
                .content(this.content)
                .createdAt(now)
                .messageType(MessageType.valueOf(messageType))
                .subjectId(this.subjectId)
                .build();
    }

}
