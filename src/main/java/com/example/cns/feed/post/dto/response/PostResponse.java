package com.example.cns.feed.post.dto.response;


import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostResponse(
        Long id,
        PostMember postMember,
        String content,
        LocalDateTime createdAt,
        int likeCnt,
        int mentionCnt,
        int fileCnt,
        boolean isCommentEnabled
) {
}
