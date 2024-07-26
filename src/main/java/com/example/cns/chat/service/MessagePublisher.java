package com.example.cns.chat.service;

import com.example.cns.chat.dto.response.ChatResponse;
import com.example.cns.chat.dto.response.UnRead;

import java.util.List;

public interface MessagePublisher {
    void publishMessage(Long roomId, ChatResponse chat);

    void publishUnRead(Long roomId, List<UnRead> unReadList);
}
