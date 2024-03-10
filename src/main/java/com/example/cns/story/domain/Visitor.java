package com.example.cns.story.domain;

import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
@Entity
@Getter
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    @ColumnDefault("false")
    private boolean isLike;
}
