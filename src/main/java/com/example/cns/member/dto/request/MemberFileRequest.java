package com.example.cns.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "회원 프로필 사진, 이력서 업로드 요청 DTO")
public record MemberFileRequest(
        @Schema(description = "파일")
        MultipartFile file
) {
}
