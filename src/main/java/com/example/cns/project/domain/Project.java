package com.example.cns.project.domain;

import com.example.cns.company.domain.Company;
import com.example.cns.member.domain.Member;
import com.example.cns.plan.domain.Plan;
import com.example.cns.project.dto.request.ProjectPatchRequest;
import com.example.cns.projectPost.domain.ProjectPost;
import com.example.cns.task.domain.Task;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member manager;

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false)
    private String detail;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false)
    private LocalDate start;

    @Column(nullable = false)
    private LocalDate end;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectPost> posts = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plan> plans = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    @Builder
    public Project(Company company, Member manager, String projectName, String detail, String goal, LocalDate start, LocalDate end) {
        this.company = company;
        this.manager = manager;
        this.projectName = projectName;
        this.detail = detail;
        this.goal = goal;
        this.start = start;
        this.end = end;
    }

    public void setManager(Member member) {
        this.company = member.getCompany();
        this.manager = member;
    }

    public void updateProject(ProjectPatchRequest projectPatchRequest) {
        this.projectName = projectPatchRequest.projectName();
        this.detail = projectPatchRequest.detail();
        this.goal = projectPatchRequest.goal();
        this.start = projectPatchRequest.start();
        this.end = projectPatchRequest.end();
    }

}
