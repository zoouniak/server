package com.example.cns.project.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "프로젝트 수정 요청 DTO")
public record ProjectPatchRequest(
        @NotBlank
        @Schema(description = "프로젝트 이름")
        String projectName,
        @NotBlank
        @Schema(description = "프로젝트 설명")
        String detail,
        @NotBlank
        @Schema(description = "프로젝트 목표")
        String goal,
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        @Schema(description = "프로젝트 시작날짜")
        LocalDate start,
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        @Schema(description = "프로젝트 마감날짜")
        LocalDate end
) {
}
