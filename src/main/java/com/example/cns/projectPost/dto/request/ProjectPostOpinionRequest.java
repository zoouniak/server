package com.example.cns.projectPost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 게시글 의견 추가 요청 DTO")
public record ProjectPostOpinionRequest(
        @Schema(description = "프로젝트 의견 종류", example = "[PROS,CONS,CHECK]")
        String type
) {
}
