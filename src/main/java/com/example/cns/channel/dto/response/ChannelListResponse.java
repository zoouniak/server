package com.example.cns.channel.dto.response;

import com.example.cns.feed.post.dto.response.PostMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "채널 정보 DTO")
@Builder
public record ChannelListResponse(
        @Schema(description = "채널 인덱스 값")
        Long id,
        @Schema(description = "채널 명")
        String name,
        @Schema(description = "채널 참여자 정보")
        List<PostMember> members

) {
}
