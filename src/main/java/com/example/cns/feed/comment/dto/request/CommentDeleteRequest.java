package com.example.cns.feed.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "댓글 삭제 요청 DTO")
public record CommentDeleteRequest(
        @NotNull
        @Schema(description = "게시글 인덱스")
        Long postId,
        @NotNull
        @Schema(description = "댓글 인덱스")
        Long commentId
) {
}
