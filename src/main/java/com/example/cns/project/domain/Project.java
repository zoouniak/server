package com.example.cns.project.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false)
    private String goal;

}
