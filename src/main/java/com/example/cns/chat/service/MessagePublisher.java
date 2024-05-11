package com.example.cns.chat.service;

import com.example.cns.chat.dto.request.ImageMessageFormat;
import com.example.cns.chat.dto.request.TextMessageFormat;

public interface MessagePublisher {
    void publishTextMessage(Long roomId, TextMessageFormat textMessageFormat);

    void publishImageMessage(Long roomId, ImageMessageFormat imageMessageFormat);
}
