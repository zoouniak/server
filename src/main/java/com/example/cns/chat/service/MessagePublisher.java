package com.example.cns.chat.service;

import com.example.cns.chat.dto.response.ChatResponse;

public interface MessagePublisher {
    void publishMessage(Long roomId, ChatResponse chat);
}
