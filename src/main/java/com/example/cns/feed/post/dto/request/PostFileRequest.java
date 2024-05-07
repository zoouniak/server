package com.example.cns.feed.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "파일 등록 dto")
public record PostFileRequest(
        @Schema(description = "여러개의 파일")
        List<MultipartFile> files
){
}
