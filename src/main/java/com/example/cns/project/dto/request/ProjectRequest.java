package com.example.cns.project.dto.request;

import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "프로젝트 등록/수정 요청 DTO")
public record ProjectRequest(
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
    public Project toEntity(Member member){
        return Project.builder()
                .company(member.getCompany())
                .manager(member)
                .projectName(projectName)
                .detail(detail)
                .goal(goal)
                .start(start)
                .end(end)
                .build();
    }
}
