package com.example.cns.chat.dto;

import com.example.cns.chat.domain.Chat;
import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.type.MessageType;
import com.example.cns.member.domain.Member;
import lombok.NonNull;

public record MessageFormat(
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
    public Chat toChatEntity(ChatRoom room, Member from) {
        return Chat.builder()
                .chatRoom(room)
                .from(from)
                .content(this.content)
                .messageType(MessageType.valueOf(messageType))
                .subjectId(this.subjectId)
                .build();
    }
}
