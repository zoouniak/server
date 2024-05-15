package com.example.cns.member.dto.request;

import java.time.LocalDate;

public record MemberProfilePatchRequest(
        String introduction,

        LocalDate birth
) {
}
