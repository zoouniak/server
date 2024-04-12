package com.example.cns.common.security.jwt.dto;

import com.example.cns.member.type.RoleType;

public record JwtMemberInfo(
        Long memberId,
        RoleType role
) {
}
