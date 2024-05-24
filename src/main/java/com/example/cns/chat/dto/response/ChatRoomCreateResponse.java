package com.example.cns.chat.dto.response;

public record ChatRoomCreateResponse(
        Long roomId,
        String inviteMsg
) {
}
