package com.example.cns.project.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PlanDetailResponse(
        String planName,
        LocalDateTime startedAt,


        LocalDateTime endedAt,

        String content,

        List<MemberResponse> participants
) {

}
