package com.example.cns.project.plan.domain;

import com.example.cns.project.domain.Project;
import com.example.cns.project.dto.request.PlanCreateRequest;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String planName;

    @Column
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime endedAt;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    public Plan(String planName, LocalDateTime startedAt, LocalDateTime endedAt, String content, Project project) {
        this.planName = planName;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.content = content;
        this.project = project;
    }

    public void updatePlan(PlanCreateRequest planEditRequest) {
        this.planName = planEditRequest.planName();
        this.content = planEditRequest.content();
        this.startedAt = planEditRequest.startedAt();
        this.endedAt = planEditRequest.endedAt();
    }
}
