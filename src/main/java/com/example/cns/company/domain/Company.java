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

    public Company(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
