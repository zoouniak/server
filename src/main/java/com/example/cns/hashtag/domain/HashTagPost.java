package com.example.cns.hashtag.domain;

import com.example.cns.feed.post.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "hashtag_post")
@Entity
@Getter
public class HashTagPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hashtag_id")
    private HashTag hashtag;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
