package com.example.cns.story.domain;

import com.example.cns.common.FileEntity;
import com.example.cns.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Story extends FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(updatable = false)
    private LocalDateTime createdAt;
}
