package com.example.cns.chat.event;

public record UserUnsubscribeEvent(Long memberId, Long roomId) {
}
