package com.example.cns.task.domain;

import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;
import com.example.cns.task.type.TodoState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull
    @Size(min = 1)
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TodoState state;

    @Builder
    public Task(Member member, Project project, String content, TodoState state) {
        this.member = member;
        this.project = project;
        this.content = content;
        this.state = state;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateState(TodoState state) {
        this.state = state;
    }
}
