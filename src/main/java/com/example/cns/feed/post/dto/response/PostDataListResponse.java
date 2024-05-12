package com.example.cns.feed.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "게시글관련 데이터 응답 DTO")
public record PostDataListResponse(
        @Schema(description = "게시글 언급 리스트")
        List<String> mentions,
        @Schema(description = "게시글 해시태그 리스트")
        List<String> hashtags
) {
    @Builder
    public PostDataListResponse(List<String> mentions, List<String> hashtags){
        this.mentions = mentions;
        this.hashtags = hashtags;
    }
}
