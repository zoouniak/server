package com.example.cns.chat.dto.request;

import java.util.List;

public record MemberAddRequest(
        List<Long> inviteList
) {
}
