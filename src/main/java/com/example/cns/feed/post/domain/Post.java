package com.example.cns.feed.post.domain;

import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String content;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    @ColumnDefault("0")
    private int like_cnt;

    @Column
    @ColumnDefault("0")
    private int file_cnt;

    @Column
    @ColumnDefault("0")
    private int mention_cnt;

    @Column
    @ColumnDefault("true")
    private boolean isCommentEnabled;

    @Builder
    public Post(Member member, String content, int mention_cnt, boolean isCommentEnabled){
        this.member = member;
        this.content = content;
        this.mention_cnt = mention_cnt;
        this.isCommentEnabled = isCommentEnabled;
    }
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
