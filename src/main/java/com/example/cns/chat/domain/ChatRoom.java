package com.example.cns.chat.domain;

import com.example.cns.chat.type.RoomType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column
    private String name;

    @Column
    private int memberCnt;

    @Column
    private Long lastChatId;


    @Builder
    public ChatRoom(RoomType roomType, String name, int memberCnt) {
        this.roomType = roomType;
        this.name = name;
        this.memberCnt = memberCnt;
    }

    public void decreaseMemberCnt() {
        memberCnt--;
    }

    public void updateLastChat(Long chatId) {
        this.lastChatId = chatId;
    }
}
