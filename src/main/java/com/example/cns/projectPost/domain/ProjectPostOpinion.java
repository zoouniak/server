package com.example.cns.projectPost.domain;

import com.example.cns.member.domain.Member;
import com.example.cns.projectPost.type.OpinionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectPostOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_post_id")
    private ProjectPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    @Enumerated(EnumType.STRING)
    private OpinionType opinionType;

    @Builder
    public ProjectPostOpinion(ProjectPost post, Member member, OpinionType opinionType) {
        this.post = post;
        this.member = member;
        this.opinionType = opinionType;
    }

    @PrePersist
    public void prePersist() {
        post.incrementOpinionCount(opinionType);
    }

    @PreRemove
    public void preRemove() {
        post.decrementOpinionCount(opinionType);
    }
}
