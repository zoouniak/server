package com.example.cns.project.plan.domain;

import com.example.cns.project.domain.Project;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String goal;

    @Column
    private String planName;

    @Column
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime endedAt;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;


}
