package com.example.cns.feed.comment.dto.response;

import com.example.cns.feed.post.dto.response.MentionInfo;
import com.example.cns.feed.post.dto.response.PostMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "댓글 조회 응답 DTO")
public record CommentResponse(
        @Schema(description = "댓글 인덱스")
        Long commentId,
        @Schema(description = "작성자 정보")
        PostMember postMember,
        @Schema(description = "댓글 내용")
        String content,
        @Schema(description = "좋아요 개수")
        int likeCnt,
        @Schema(description = "작성 시각")
        LocalDateTime createdAt,
        @Schema(description = "대댓글 개수")
        int commentReplyCnt,
        @Schema(description = "좋아요 여부")
        boolean liked,
        List<MentionInfo> mentions
) {
    @Builder
    public CommentResponse(Long commentId, PostMember postMember, String content, int likeCnt, LocalDateTime createdAt, int commentReplyCnt, boolean liked, List<MentionInfo> mentions) {
        this.commentId = commentId;
        this.postMember = postMember;
        this.content = content;
        this.likeCnt = likeCnt;
        this.createdAt = createdAt;
        this.commentReplyCnt = commentReplyCnt;
        this.liked = liked;
        this.mentions = mentions;
    }

    public CommentResponse(Long commentId, Long memberId, String nickname, String profile, String content, int likeCnt, LocalDateTime createdAt, int commentReplyCnt, boolean liked) {
        this(commentId, new PostMember(memberId, nickname, profile), content, likeCnt, createdAt, commentReplyCnt, liked, new ArrayList<>());
    }

    public CommentResponse withData(List<MentionInfo> mentions) {
        return new CommentResponse(commentId, postMember, content, likeCnt, createdAt, commentReplyCnt, liked, mentions);
    }
}
