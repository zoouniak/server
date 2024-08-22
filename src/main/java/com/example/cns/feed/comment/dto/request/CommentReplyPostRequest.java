package com.example.cns.feed.comment.dto.request;

import com.example.cns.feed.comment.domain.Comment;
import com.example.cns.feed.post.domain.Post;
import com.example.cns.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "대댓글 등록 요청 DTO")
public record CommentReplyPostRequest(
        @NotNull
        @Schema(description = "게시글 인덱스")
        Long postId,
        @NotNull
        @Schema(description = "댓글 인덱스")
        Long commentId,
        @NotBlank
        @Schema(description = "대댓글 내용")
        String content,
        @Schema(description = "멘션 리스트", defaultValue = "null", example = "['@사용자1','@사용자2']")
        List<String> mention
) {
    public Comment toEntity(Member member, Post post, Comment comment) {
        return Comment.builder()
                .writer(member)
                .post(post)
                .content(content)
                .parentComment(comment)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
