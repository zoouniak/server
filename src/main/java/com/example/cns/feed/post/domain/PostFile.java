package com.example.cns.feed.post.domain;

import com.example.cns.common.FileEntity;
import com.example.cns.common.type.FileType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "post_file")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFile extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostFile(Post post, String url, String fileName, LocalDateTime createdAt, FileType fileType){
        this.post = post;
        this.setUrl(url);
        this.setFileName(fileName);
        this.setCreatedAt(createdAt);
        this.setFileType(fileType);
    }
}
