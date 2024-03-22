package com.example.cns.common.security.jwt.dto;

import com.example.cns.member.type.RoleType;

public record JwtUserInfo(
        Long memberId,
        RoleType role
) {
}
