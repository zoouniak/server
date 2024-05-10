package com.example.cns.hashtag.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity(name = "hashtag_view")
@Immutable
@Getter
@NoArgsConstructor
public class HashTagView {

    @Id
    @Column(name = "hashtag_id")
    private Long hashtagId;
    @Column(name = "hashtag_name")
    private String name;
    @Column(name = "post_count")
    private Long postCnt;

}
