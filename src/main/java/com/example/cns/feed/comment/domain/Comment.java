package com.example.cns.feed.comment.domain;

import com.example.cns.feed.post.domain.Post;
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
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column
    private String content;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    @ColumnDefault("0")
    private int likeCnt;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE)
    private List<Comment> childComments;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentLike> commentLikes;

    @Builder
    public Comment(Member writer, Post post, Comment parentComment, String content, LocalDateTime createdAt) {
        this.writer = writer;
        this.post = post;
        this.parentComment = parentComment;
        this.content = content;
        this.createdAt = createdAt;
    }

    public void plusLikeCnt(){
        this.likeCnt += 1;
    }

    public void minusLikeCnt(){
        this.likeCnt -= 1;
    }
}
