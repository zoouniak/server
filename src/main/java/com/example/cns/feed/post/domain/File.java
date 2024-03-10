package com.example.cns.feed.post.domain;

import com.example.cns.common.FileEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "post_file")
@Entity
@Getter
public class File extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

}
