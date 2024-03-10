package com.example.cns.feed.post.domain;

import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column @ColumnDefault("true")
    private boolean commentFlag;
}
