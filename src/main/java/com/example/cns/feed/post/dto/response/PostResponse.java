package com.example.cns.feed.post.dto.response;


import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        PostMember postMember,
        String content,
        LocalDateTime createdAt,
        int like_cnt,
        int mention_cnt,
        boolean isCommentEnabled

) {
}
