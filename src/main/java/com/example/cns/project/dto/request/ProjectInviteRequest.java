package com.example.cns.project.dto.request;

import java.util.List;

public record ProjectInviteRequest(

        Long managerId,
        List<Long> memberList
) {
}
