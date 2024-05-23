package com.example.cns.project.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "프로젝트 수정 요청 DTO")
public record ProjectPatchRequest(
        @Schema(description = "프로젝트 이름")
        String projectName,
        @Schema(description = "프로젝트 설명")
        String detail,
        @Schema(description = "프로젝트 목표")
        String goal,
        @Schema(description = "프로젝트 시작날짜")
        LocalDate start,
        @Schema(description = "프로젝트 마감날짜")
        LocalDate end
) {
}
