package com.example.cns.plan.dto.response;

import java.time.LocalDateTime;

public record PlanListResponse(
        String planName,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {

}
