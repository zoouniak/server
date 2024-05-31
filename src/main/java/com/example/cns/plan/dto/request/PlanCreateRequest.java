package com.example.cns.plan.dto.request;

import com.example.cns.plan.domain.Plan;
import com.example.cns.project.domain.Project;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record PlanCreateRequest(

        @NotBlank
        String planName,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime startedAt,

        @Future
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime endedAt,

        @NotBlank
        String content,

        @NotNull
        List<Long> inviteList
) {
    public Plan toPlanEntity(Project project) {
        return Plan.builder()
                .planName(planName)
                .content(content)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .project(project)
                .build();
    }
}
