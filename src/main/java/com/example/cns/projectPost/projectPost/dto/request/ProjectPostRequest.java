package com.example.cns.projectPost.projectPost.dto.request;

import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;
import com.example.cns.projectPost.projectPost.domain.ProjectPost;

public record ProjectPostRequest(
        String content
) {
    public ProjectPost toEntity(Member member, Project project){
        return ProjectPost.builder()
                .project(project)
                .member(member)
                .content(content)
                .build();
    }
}
