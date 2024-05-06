package com.example.cns.feed.post.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PostPatchRequest(
        @NotBlank
        String content,
        List<String> hashtag,
        List<String> mention,
        boolean isCommentEnabled
) {
}
