package com.example.cns.chat.domain;

import com.example.cns.chat.type.RoomType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String lastChat;

    @Column
    private LocalDateTime lastChatSendAt;

    @OneToMany(mappedBy = "room")
    private List<ChatParticipation> participationList;

    @Builder
    public ChatRoom(RoomType roomType, String name, int memberCnt) {
        this.roomType = roomType;
        this.name = name;
        this.memberCnt = memberCnt;
        this.participationList = new ArrayList<>();
    }

    public void saveLastChat(String chat, LocalDateTime now) {
        this.lastChat = chat;
        this.lastChatSendAt = now;
    }
}
