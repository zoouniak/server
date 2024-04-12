package com.example.cns.hashtag.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "hashtag")
@Entity
@Getter
public class HashTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,updatable = false)
    private String name;
}
