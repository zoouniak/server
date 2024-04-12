package com.example.cns.member.type;

import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.security.exception.AuthException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum RoleType {
    EMPLOYEE("ROLE_EMPLOYEE"),

    ADMIN("ROLE_ADMIN"),
    GENERAL("ROLE_GENERAL");
    private final String role;

    public static RoleType fromRoleString(String role) {
        return Arrays.stream(RoleType.values())
                .filter(roleType -> roleType.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new AuthException(ExceptionCode.INVALID_ROLE));
    }

    public String getRole() {
        return role;
    }

}
