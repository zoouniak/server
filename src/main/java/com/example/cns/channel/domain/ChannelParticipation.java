package com.example.cns.channel.domain;

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
@IdClass(ChannelParticipationID.class)
public class ChannelParticipation implements Persistable<ChannelParticipationID> {
    @Id
    @Column(name = "member_id")
    private Long member;

    @Id
    @Column(name = "channel_id")
    private Long channel;

    @Builder
    public ChannelParticipation(Long member, Long channel) {
        this.member = member;
        this.channel = channel;
    }

    @Override
    public ChannelParticipationID getId() {
        return new ChannelParticipationID(this.member, this.channel);
    }

    @Override
    public boolean isNew() {
        return this.member != null && this.channel != null;
    }
}
