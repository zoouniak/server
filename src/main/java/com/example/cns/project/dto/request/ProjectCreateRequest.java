package com.example.cns.project.dto.request;

import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "프로젝트 등록 요청 DTO")
public record ProjectCreateRequest(
        @Schema(description = "프로젝트 이름")
        String projectName,
        @Schema(description = "프로젝트 설명")
        String detail,
        @Schema(description = "프로젝트 목표")
        String goal,
        @Schema(description = "프로젝트 시작날짜")
        LocalDate start,
        @Schema(description = "프로젝트 마감날짜")
        LocalDate end,
        @Schema(description = "담당자 인덱스 값")
        Long managerId,
        @Schema(description = "참여자 인덱스 값 리스트")
        List<Long> memberList
) {
    public Project toProjectEntity(Member manager){
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
