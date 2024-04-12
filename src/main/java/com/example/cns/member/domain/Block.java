package com.example.cns.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Block {
    @Id
    @Column(name = "from_id")
    private Long blocker;

    @Id
    @Column(name = "to_id")
    private Long target;

}
