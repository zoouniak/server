package com.example.cns.company.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, insertable = false, updatable = false)
    private String name;

    @Column(unique = true, insertable = false, updatable = false)
    private String email;

    @Column(unique = true, insertable = false, updatable = false)
    private String field;

    public Company(Long id, String name, String email, String field) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.field = field;
    }
}
