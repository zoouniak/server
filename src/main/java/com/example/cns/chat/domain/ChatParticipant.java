package com.example.cns.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class ChatParticipant {
    @Id
    @Column(name = "member_id")
    private Long member;

    @Id
    @Column(name = "room_id")
    private Long room;
}
