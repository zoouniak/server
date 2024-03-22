package com.example.cns.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class ProjectParticipant {
    @Id
    @Column(name = "member_id")
    private Long member;

    @Id
    @Column(name = "project_id")
    private Long project;
}
