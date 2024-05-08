package com.example.cns.chat.service;

import com.example.cns.chat.dto.request.MessageFormat;

public interface MessagePublisher {
    void publish(Long roomId, MessageFormat messageFormat);
}
