package com.example.cns.feed.post.dto.request;

import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.dto.response.FileResponse;
import com.example.cns.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "게시물 등록 요청 DTO")
public record PostRequest(
        @NotBlank
        @Schema(description = "내용")
        String content,
        @Schema(description = "해시태그 리스트", defaultValue = "null", example = "['해시태그','해시_태그']")
        List<String> hashtag,
        @Schema(description = "멘션 리스트", defaultValue = "null", example = "['@사용자1','@사용자2']")
        List<String> mention,
        @Schema(description = "댓글 허용 여부", defaultValue = "True")
        boolean isCommentEnabled,
        @Schema(description = "업로드 된 사진 정보 리스트", defaultValue = "null")
        List<FileResponse> postFileList
) {
    public Post toEntity(Member member) {
        return Post.builder()
                .member(member)
                .content(content)
                .mentionCnt(mention.size())
                .fileCnt(postFileList.size())
                .isCommentEnabled(isCommentEnabled)
                .build();
    }
}
