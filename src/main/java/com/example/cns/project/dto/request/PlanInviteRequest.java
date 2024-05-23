package com.example.cns.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "일정 초대 요청 DTO")
public record PlanInviteRequest(
        @Schema(description = "참여자 인덱스 값 리스트")
        List<Long> memberList
) {
}
