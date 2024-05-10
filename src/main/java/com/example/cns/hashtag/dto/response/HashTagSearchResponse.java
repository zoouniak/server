package com.example.cns.hashtag.dto.response;

import lombok.Builder;

public record HashTagSearchResponse(
        String name,
        Long postCnt
) {
    @Builder
    public HashTagSearchResponse(String name, Long postCnt) {
        this.name = name;
        this.postCnt = postCnt;
    }
}
