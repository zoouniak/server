package com.example.cns.feed.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "게시글 좋아요 요청 DTO")
public record PostLikeRequest(
        @NotNull
        @Schema(description = "게시글 인덱스")
        Long postId
) {
}
