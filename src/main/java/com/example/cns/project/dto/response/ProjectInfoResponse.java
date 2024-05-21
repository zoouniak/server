package com.example.cns.project.dto.response;

import com.example.cns.feed.post.dto.response.PostMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "프로젝트 정보 반환 DTO")
@Builder
public record ProjectInfoResponse(
        @Schema(description = "프로젝트 인덱스 값")
        Long projectId,
        @Schema(description = "프로젝트 이름")
        String projectName,
        @Schema(description = "프로젝트 설명")
        String detail,
        @Schema(description = "담당자 정보")
        PostMember postMember
) {
}
