package com.example.cns.project.projectPost.domain;

import com.example.cns.project.domain.Project;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class projectPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column
    private Long opinionCnt;

    @Column
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
}
