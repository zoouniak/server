package com.example.cns.chat.dto.response;

import com.example.cns.chat.type.RoomType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatRoomResponse(
        Long roomId,
        String roomName,
        String lastChat,
        // LocalDateTime 배열로 나오는 문제 해결
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime lastChatSendAt,
        RoomType roomType,
        boolean isRead
) {

}
