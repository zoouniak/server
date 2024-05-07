package com.example.cns.chat.domain;

import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
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
    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoom room;

    @Id
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    
    private boolean isRead;

    @Builder
    public ChatParticipation(Member member, ChatRoom room) {
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
