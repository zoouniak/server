package com.example.cns.common;

import com.example.cns.common.type.FileType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class FileEntity {
    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String uuid;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}
