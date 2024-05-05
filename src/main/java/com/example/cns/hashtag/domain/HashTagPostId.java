package com.example.cns.hashtag.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashTagPostId implements Serializable {
    @Column(name = "hashtag_id")
    private Long hashtag;

    @Column(name = "post_id")
    private Long post;

    @Builder
    public HashTagPostId(Long hashtagId, Long postId){
        this.hashtag = hashtagId;
        this.post = postId;
    }
}
