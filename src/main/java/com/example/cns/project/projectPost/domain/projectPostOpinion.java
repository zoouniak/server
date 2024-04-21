package com.example.cns.project.projectPost.domain;

import com.example.cns.project.projectPost.type.OpinionType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class projectPostOpinion {
    private Long id;
    private OpinionType opinionType;
    @ManyToOne
    @JoinColumn(name = "project_post_id")
    private projectPost post;
}
