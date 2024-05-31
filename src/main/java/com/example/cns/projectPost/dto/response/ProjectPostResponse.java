package com.example.cns.projectPost.dto.response;

import com.example.cns.feed.post.dto.response.PostMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Schema(description = "프로젝트 게시글 조회 반환 DTO")
@Getter
@Setter
@NoArgsConstructor
public class ProjectPostResponse {

    @Schema(description = "프로젝트 게시글 인덱스값")
    private Long id;
    @Schema(description = "프로젝트 인덱스값")
    private Long projectId;
    @Schema(description = "프로젝트 게시글 작성자 정보")
    private PostMember postMember;
    @Schema(description = "프로젝트 게시글 내용")
    private String content;
    @Schema(description = "프로젝트 게시글 작성시각")
    private LocalDateTime createdAt;
    @Schema(description = "찬성 개수")
    private int prosCnt;
    @Schema(description = "반대 개수")
    private int consCnt;
    @Schema(description = "확인 개수")
    private int checkCnt;
    @Schema(description = "현재 사용자가 의견을 남겼는지 여부")
    private String opinion;

    @Builder
    public ProjectPostResponse(Long id, Long projectId, Long memberId, String nickname, String url, String content, LocalDateTime createdAt,
                               int prosCnt, int consCnt, int checkCnt, String opinion) {
        this.id = id;
        this.projectId = projectId;
        this.postMember = PostMember.builder().id(memberId).nickname(nickname).profile(url).build();
        this.content = content;
        this.createdAt = createdAt;
        this.prosCnt = prosCnt;
        this.consCnt = consCnt;
        this.checkCnt = checkCnt;
        this.opinion = opinion;
    }
}
