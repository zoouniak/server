package com.example.cns.feed.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        boolean liked,
        List<Long> mentions,
        List<String> hashtags
) {
    @Builder
    public PostResponse(Long id, PostMember postMember, String content, LocalDateTime createdAt, int likeCnt, int fileCnt, int commentCnt, boolean isCommentEnabled, boolean liked,List<Long> mentions, List<String> hashtags) {
        this.id = id;
        this.postMember = postMember;
        this.content = content;
        this.createdAt = createdAt;
        this.likeCnt = likeCnt;
        this.fileCnt = fileCnt;
        this.commentCnt = commentCnt;
        this.isCommentEnabled = isCommentEnabled;
        this.liked = liked;
        this.mentions = mentions;
        this.hashtags = hashtags;
    }

    public PostResponse(Long id, Long memberId, String nickname, String profile, String content, LocalDateTime createdAt, int likeCnt, int fileCnt, int commentCnt, boolean isCommentEnabled, boolean liked) {
        this(id, new PostMember(memberId, nickname, profile), content, createdAt, likeCnt, fileCnt, commentCnt, isCommentEnabled, liked,new ArrayList<>(),new ArrayList<>());
    }

    public PostResponse withData(List<Long> mentions, List<String> hashtags) {
        return new PostResponse(id, postMember, content, createdAt, likeCnt, fileCnt, commentCnt, isCommentEnabled, liked, mentions, hashtags);
    }


}
