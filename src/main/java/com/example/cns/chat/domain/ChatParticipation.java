package com.example.cns.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Entity
@NoArgsConstructor
@Getter
@IdClass(ChatParticipationID.class)
public class ChatParticipation implements Persistable<ChatParticipationID> {
    @Id
    @Column(name = "room_id")
    private Long room;

    @Id
    @Column(name = "member_id")
    private Long member;

    private boolean isRead;

    @Builder
    public ChatParticipation(Long member, Long room) {
        this.member = member;
        this.room = room;
        this.isRead = false;
    }

    @Override
    public ChatParticipationID getId() {
        return new ChatParticipationID(this.member, this.room);
    }

    @Override
    public boolean isNew() {
        return this.member != null && this.room != null;
    }
}
