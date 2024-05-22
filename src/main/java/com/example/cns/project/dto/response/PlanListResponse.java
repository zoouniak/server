package com.example.cns.project.dto.response;

import java.time.LocalDateTime;

public record PlanListResponse(
        String planName,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {

}
