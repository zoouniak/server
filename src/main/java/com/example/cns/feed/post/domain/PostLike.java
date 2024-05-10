package com.example.cns.feed.post.domain;

import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "post_like")
@Entity
@Getter
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

}
