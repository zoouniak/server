package com.example.cns.feed.post.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentionInfo {
    private String nickname;
    private Long memberId;

    @Builder
    public MentionInfo(String nickname, Long memberId){
        this.nickname = nickname;
        this.memberId = memberId;
    }
}
