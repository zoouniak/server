package com.example.cns.feed.post.dto.request;

import com.example.cns.feed.post.dto.response.FileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "게시글 수정 요청 DTO")
public record PostPatchRequest(
        @NotBlank
        @Schema(description = "수정된 게시글 내용")
        String content,
        @Schema(description = "수정된 해시태그 내용")
        List<String> hashtag,
        @Schema(description = "수정된 멘션 내용")
        List<String> mention,
        @Schema(description = "수정된 댓글 허용 여부")
        boolean isCommentEnabled,
        @Schema(description = "수정된 미디어 내용")
        List<FileResponse> postFileList
) {
}
