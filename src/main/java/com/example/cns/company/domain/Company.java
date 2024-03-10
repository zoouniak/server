package com.example.cns.company.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(insertable = false, updatable = false)
    private String name;

    @Column(insertable = false, updatable = false)
    private String email;
}
