package com.example.cns.feed.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 좋아요 요청 DTO")
public record PostLikeRequest(
        @Schema(description = "게시글 인덱스")
        Long postId
) {
}
