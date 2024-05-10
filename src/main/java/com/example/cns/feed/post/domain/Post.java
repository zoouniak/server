package com.example.cns.feed.post.domain;

import com.example.cns.feed.comment.domain.Comment;
import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private int likeCnt;

    @Column
    @ColumnDefault("0")
    private int fileCnt;

    @Column
    @ColumnDefault("0")
    private int mentionCnt;

    @Column
    @ColumnDefault("true")
    private boolean isCommentEnabled;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostFile> postFiles = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(Member member, String content, int mentionCnt, int fileCnt, boolean isCommentEnabled) {
        this.member = member;
        this.content = content;
        this.mentionCnt = mentionCnt;
        this.fileCnt = fileCnt;
        this.isCommentEnabled = isCommentEnabled;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        if (content != null) this.content = content;
    }

    public void updateMentionCnt(int mentionCnt) {
        this.mentionCnt = mentionCnt;
    }

    public void updateIsCommentEnabled(Boolean isCommentEnabled) {
        this.isCommentEnabled = isCommentEnabled;
    }

    public void updateFileCnt(int fileCnt) {
        this.fileCnt = fileCnt;
    }
}
