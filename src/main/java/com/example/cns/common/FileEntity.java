package com.example.cns.common;

import com.example.cns.common.type.FileType;
import jakarta.persistence.Column;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class FileEntity {
    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private FileType type;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String uuid;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}
