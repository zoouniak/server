package com.example.cns.mention.domain;

import com.example.cns.member.domain.Member;
import com.example.cns.mention.type.MentionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_id")
    private Member member;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column
    @Enumerated(EnumType.STRING)
    private MentionType mentionType;

    @Builder
    public Mention(Member member, Long subjectId, MentionType mentionType) {
        this.member = member;
        this.subjectId = subjectId;
        this.mentionType = mentionType;
    }

}
