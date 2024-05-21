package com.example.cns.project.dto.request;

import com.example.cns.company.domain.Company;
import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;

import java.time.LocalDate;

public record ProjectRequest(
        String projectName,
        String detail,
        String goal,
        LocalDate start,
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
