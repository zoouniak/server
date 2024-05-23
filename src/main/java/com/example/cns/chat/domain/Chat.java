package com.example.cns.chat.domain;

import com.example.cns.chat.type.MessageType;
import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member from;

    @Column(nullable = false)
    private String content;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column
    private Long subjectId;

    @Builder
    public Chat(ChatRoom chatRoom, Member from, String content, LocalDateTime createdAt, MessageType messageType, Long subjectId) {
        this.chatRoom = chatRoom;
        this.from = from;
        this.content = content;
        this.createdAt = createdAt;
        this.messageType = messageType;
        this.subjectId = subjectId;
    }
}
