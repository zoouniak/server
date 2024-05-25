package com.example.cns.projectPost.projectPost.domain;

import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;
import com.example.cns.projectPost.projectPost.type.OpinionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String content;

    @Column(name = "pros_cnt")
    @ColumnDefault("0")
    private int prosCnt;

    @Column(name = "cons_cnt")
    @ColumnDefault("0")
    private int consCnt;

    @Column(name = "check_cnt")
    @ColumnDefault("0")
    private int checkCnt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectPostOpinion> opinions = new ArrayList<>();

    @Builder
    public ProjectPost(Project project, Member member, String content){
        this.project = project;
        this.member = member;
        this.content = content;
        this.prosCnt = 0;
        this.consCnt = 0;
        this.checkCnt = 0;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void updateContent(String content){
        this.content = content;
    }

    public void incrementOpinionCount(OpinionType opinionType) {
        switch (opinionType) {
            case CONS -> this.consCnt++;
            case PROS -> this.prosCnt++;
            case CHECK -> this.checkCnt++;
        }
    }

    public void decrementOpinionCount(OpinionType opinionType) {
        switch (opinionType) {
            case CONS -> this.consCnt--;
            case PROS -> this.prosCnt--;
            case CHECK -> this.checkCnt--;
        }
    }
}
