package com.example.cns.project.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ProjectResponse(

        Long projectId,
        String projectName,
        String detail,
        String goal,
        LocalDate start,
        LocalDate end
) {
}
