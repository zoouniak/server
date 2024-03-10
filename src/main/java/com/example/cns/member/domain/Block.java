package com.example.cns.member.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Block {
    @Id
    @Column(name="blocker")
    private Long blocker;

    @Id
    @Column(name = "target")
    private Long target;

}
