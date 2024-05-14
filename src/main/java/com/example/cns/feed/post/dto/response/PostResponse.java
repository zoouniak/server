package com.example.cns.feed.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "게시글 조회 응답 DTO")
public record PostResponse(
        @Schema(description = "게시글 인덱스")
        Long id,
        @Schema(description = "작성자 정보")
        PostMember postMember,
        @Schema(description = "게시글 내용")
        String content,
        @Schema(description = "작성 시각")
        LocalDateTime createdAt,
        @Schema(description = "좋아요 개수")
        int likeCnt,
        @Schema(description = "게시글 파일 개수")
        int fileCnt,
        @Schema(description = "게시글 댓글 개수")
        int commentCnt,
        @Schema(description = "게시글 댓글 허용 여부")
        boolean isCommentEnabled,
        @Schema(description = "사용자 좋아요 여부")
        boolean liked
) {
    @Builder
    public PostResponse(Long id, PostMember postMember, String content, LocalDateTime createdAt, int likeCnt, int fileCnt, int commentCnt, boolean isCommentEnabled, boolean liked) {
        this.id = id;
        this.postMember = postMember;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCnt = likeCnt;
        this.fileCnt = fileCnt;
        this.commentCnt = commentCnt;
        this.isCommentEnabled = isCommentEnabled;
        this.liked = liked;
    }
}
