package com.example.cns.member.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record MemberFileRequest(
        MultipartFile file
) {
}
