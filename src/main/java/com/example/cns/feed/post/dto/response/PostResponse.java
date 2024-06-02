package com.example.cns.feed.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private PostMember postMember;
    private String content;
    private LocalDateTime createdAt;
    private int likeCnt;
    private int fileCnt;
    private int commentCnt;
    private boolean isCommentEnabled;
    private boolean liked;
    private List<MentionInfo> mentions;
    private List<String> hashtags;

    public PostResponse(Long id, Long memberId, String nickname, String profile, String content, LocalDateTime createdAt, int likeCnt, int fileCnt, int commentCnt, boolean isCommentEnabled, boolean liked) {
        this(id, new PostMember(memberId, nickname, profile), content, createdAt, likeCnt, fileCnt, commentCnt, isCommentEnabled, liked, new ArrayList<>(), new ArrayList<>());
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("createdAt")
    public LocalDateTime getParsedCreatedAt() {
        return createdAt;
    }

    @JsonProperty("likeCnt")
    public int getLikeCnt() {
        return likeCnt;
    }

    @JsonProperty("fileCnt")
    public int getFileCnt() {
        return fileCnt;
    }

    @JsonProperty("commentCnt")
    public int getCommentCnt() {
        return commentCnt;
    }

    @JsonProperty("isCommentEnabled")
    public boolean isCommentEnabled() {
        return isCommentEnabled;
    }

    @JsonProperty("liked")
    public boolean isLiked() {
        return liked;
    }

    @JsonProperty("postMember.id")
    public void setPostMemberId(Long id) {
        if (postMember == null) {
            postMember = new PostMember();
        }
        postMember.setId(id);
    }

    @JsonProperty("postMember.nickname")
    public void setPostMemberNickname(String nickname) {
        if (postMember == null) {
            postMember = new PostMember();
        }
        postMember.setNickname(nickname);
    }

    @JsonProperty("postMember.profile")
    public void setPostMemberProfile(String profile) {
        if (postMember == null) {
            postMember = new PostMember();
        }
        postMember.setProfile(profile);
    }

    public PostResponse withData(List<MentionInfo> mentions, List<String> hashtags) {
        return new PostResponse(id, postMember, content, createdAt, likeCnt, fileCnt, commentCnt, isCommentEnabled, liked, mentions,hashtags);
    }
}
