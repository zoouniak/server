package com.example.cns.hashtag.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    public HashTagPost(HashTagPostId id) {
        this.id = id;
    }
}
