package com.example.cns.projectPost.projectPost.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "프로젝트 게시글 의견 추가 요청 DTO")
public record ProjectPostOpinionRequest(
        @Schema(description = "프로젝트 의견 종류",example = "[PROS,CONS,CHECK]")
        @NotNull(message = "의견을 추가해주세요.")
        String type
) {
}
