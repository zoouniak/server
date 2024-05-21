package com.example.cns.project.dto.response;

import com.example.cns.feed.post.dto.response.PostMember;
import lombok.Builder;

@Builder
public record ProjectInfoResponse(
        Long projectId,
        String projectName,
        String detail,
        PostMember postMember
) {
}
