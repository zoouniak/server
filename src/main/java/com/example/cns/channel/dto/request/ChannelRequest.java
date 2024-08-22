package com.example.cns.channel.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "채널 생성/수정 DTO")
public record ChannelRequest(
        @Schema(description = "채널명")
        String name
) {
}
