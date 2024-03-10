package com.example.cns.chat.domain;

import com.example.cns.chat.type.MessageType;
import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private chatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member from;

    @Column(nullable = false)
    private String message;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Column
    private Long subjectId;
}
