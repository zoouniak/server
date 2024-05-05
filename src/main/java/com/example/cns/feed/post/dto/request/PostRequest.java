package com.example.cns.feed.post.dto.request;

import com.example.cns.feed.post.domain.Post;
import com.example.cns.member.domain.Member;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PostRequest(
        @NotBlank
        String content,
        List<String> hashtag,
        List<String> mention,
        @NotBlank
        boolean isCommentEnabled
) {
    public Post toEntity(Member member){
        String hashtags = String.join(" ", hashtag);
        String mentions = String.join(" ", mention);
        return Post.builder()
                .member(member)
                .content(content+"\n"+hashtags+"\n"+mentions)
                .mention_cnt(mention.size())
                .isCommentEnabled(isCommentEnabled)
                .build();
    }
}
