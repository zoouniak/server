package com.example.cns.follow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Follow {
    @Id
    @Column(name = "follwer")
    private Long follower;

    @Id
    @Column(name = "followee")
    private Long followee;
}
