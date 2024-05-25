package com.example.cns.projectPost.projectPost.dto.request;

import com.example.cns.member.domain.Member;
import com.example.cns.project.domain.Project;
import com.example.cns.projectPost.projectPost.domain.ProjectPost;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 게시글 작성/수정 요청 DTO")
public record ProjectPostRequest(
        @Schema(description = "프로젝트 게시글 내용")
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
