package com.example.cns.feed.post.dto.request;

import com.example.cns.feed.post.domain.Post;
import com.example.cns.feed.post.dto.response.PostFileResponse;
import com.example.cns.member.domain.Member;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PostRequest(
        @NotBlank
        String content,
        List<String> hashtag,
        List<String> mention,
        @NotBlank
        boolean isCommentEnabled,
        List<PostFileResponse> postFileList
) {
    public Post toEntity(Member member){
        return Post.builder()
                .member(member)
                .content(content)
                .mentionCnt(mention.size())
                .isCommentEnabled(isCommentEnabled)
                .build();
    }
}
