package com.example.cns.hashtag.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Table(name = "hashtag_post")
@Entity
@Getter
public class HashTagPost {
    @Id
    @Column(name = "hashtag_id")
    private Long hashtag;

    @Id
    @Column(name = "post_id")
    private Long post;
}
