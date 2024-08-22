package com.example.cns.channel.dto.response;

import com.example.cns.feed.post.dto.response.PostMember;
import lombok.Builder;

import java.util.List;

@Builder
public record ChannelListResponse(
        Long id,
        String name,
        List<PostMember> members

) {
}
