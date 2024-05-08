package com.example.cns.chat.dto.request;

import com.example.cns.chat.domain.ChatRoom;
import com.example.cns.chat.type.RoomType;

import java.util.List;

public record ChatRoomCreateRequest(
        String roomName,
        List<Long> inviteList
) {
    public ChatRoom toChatRoomEntity() {
        return ChatRoom.builder()
                .roomType(this.inviteList.size() > 2 ? RoomType.GROUP : RoomType.PERSONAL)
                .name(this.roomName)
                .memberCnt(this.inviteList.size()).build();
    }
}
