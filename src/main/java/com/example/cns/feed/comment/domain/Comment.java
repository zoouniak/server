package com.example.cns.feed.comment.domain;

import com.example.cns.feed.post.domain.Post;
import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
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
    private Comment parent;

    @Column
    private String content;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column @ColumnDefault("0")
    private int like;

    @OneToMany(mappedBy = "parent")
    private List<Comment> childComment;
}
