package com.example.cns.chat.service;

import com.example.cns.chat.dto.request.FileMessageFormat;
import com.example.cns.chat.dto.request.TextMessageFormat;
import com.example.cns.chat.dto.response.ChatResponse;

public interface MessagePublisher {
    void publishTextMessage(Long roomId, TextMessageFormat textMessageFormat);

    void publishImageMessage(Long roomId, FileMessageFormat fileMessageFormat);

    void publishMessage(Long roomId, ChatResponse chat);
}
