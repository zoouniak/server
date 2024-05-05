package com.example.cns.hashtag.domain;

import com.example.cns.feed.post.domain.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "hashtag_post")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashTagPost {

    @EmbeddedId
    private HashTagPostId id;

    @Builder
    public HashTagPost(HashTagPostId id){
        this.id = id;
    }
}
