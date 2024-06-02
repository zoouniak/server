package com.example.cns.feed.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "사용자 정보를 담은 DTO")
@Getter
@Setter
@NoArgsConstructor
public class PostMember {
    @Schema(description = "사용자 index")
    private Long id;
    @Schema(description = "사용자 아이디")
    private String nickname;
    @Schema(description = "사용자 프로필")
    private String profile;

    @Builder
    public PostMember(Long id, String nickname, String profile) {
        this.id = id;
        this.nickname = nickname;
        this.profile = profile;
    }
}
