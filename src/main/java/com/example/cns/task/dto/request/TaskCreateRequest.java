package com.example.cns.task.dto.request;

import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;
import com.example.cns.task.domain.Task;
import com.example.cns.task.type.TodoState;

public record TaskCreateRequest(
        String content,
        TodoState state
) {
    public Task toEntity(Member member, Project project) {
        return Task.builder()
                .member(member)
                .project(project)
                .content(content)
                .state(state)
                .build();
    }
}
