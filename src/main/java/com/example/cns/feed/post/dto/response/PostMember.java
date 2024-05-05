package com.example.cns.feed.post.dto.response;

import lombok.Getter;

@Getter
public class PostMember {
    private Long id;
    private String nickname;

    public PostMember(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
