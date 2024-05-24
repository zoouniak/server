package com.example.cns.project.projectPost.dto.response;

import com.example.cns.feed.post.dto.response.PostMember;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProjectPostResponse {

    private Long id;
    private Long projectId;
    private PostMember postMember;
    private String content;
    private LocalDateTime createdAt;
    private int prosCnt;
    private int consCnt;
    private int checkCnt;
    private String opinion;

    @Builder
    public ProjectPostResponse(Long id, Long projectId, Long memberId, String nickname, String url, String content, LocalDateTime createdAt,
                               int prosCnt, int consCnt, int checkCnt, String opinion){
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
