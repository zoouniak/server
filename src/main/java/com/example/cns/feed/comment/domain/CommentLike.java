package com.example.cns.feed.comment.domain;

import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
@Table(name = "comment_like")
@Entity
@Getter
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
