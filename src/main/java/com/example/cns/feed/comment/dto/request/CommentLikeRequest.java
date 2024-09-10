package com.example.cns.feed.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 좋아요 요청 DTO")
public record CommentLikeRequest(
        @NotNull
        @Schema(description = "댓글 인덱스")
        Long commentId
) {
}
