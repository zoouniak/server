package com.example.cns.auth.dto;

import lombok.NonNull;

public record EmailAuthReq(
        @NonNull
        String email,
        @NonNull
        String authCode
) {
}
