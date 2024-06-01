package com.example.cns.project.dto.request;

import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "프로젝트 등록 요청 DTO")
public record ProjectCreateRequest(
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
        LocalDate end,
        @NotNull
        @Schema(description = "담당자 인덱스 값")
        Long managerId,
        @NotNull
        @Schema(description = "참여자 인덱스 값 리스트")
        List<Long> memberList
) {
    public Project toProjectEntity(Member manager) {
        return Project.builder()
                .company(manager.getCompany())
                .manager(manager)
                .projectName(projectName)
                .detail(detail)
                .goal(goal)
                .start(start)
                .end(end)
                .build();
    }
}
