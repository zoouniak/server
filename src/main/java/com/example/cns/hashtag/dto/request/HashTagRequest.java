package com.example.cns.hashtag.dto.request;

import java.util.List;

public record HashTagRequest(
        Long postId,
        List<String> hashTags
) {
}
