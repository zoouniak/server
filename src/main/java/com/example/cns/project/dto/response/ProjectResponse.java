package com.example.cns.project.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "프로젝트 반환 DTO")
@Builder
public record ProjectResponse(
        @Schema(description = "프로젝트 인덱스 값")
        Long projectId,
        @Schema(description = "프로젝트 이름")
        String projectName,
        @Schema(description = "프로젝트 설명")
        String detail,
        @Schema(description = "프로젝트 목표")
        String goal,
        @Schema(description = "프로젝트 시작 날짜")
        LocalDate start,
        @Schema(description = "프로젝트 마감 날짜")
        LocalDate end
) {
}
