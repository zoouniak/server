package com.example.cns.feed.post.dto.response;

import com.example.cns.common.type.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "게시물 미디어 등록 응답 DTO")
public record PostFileResponse(
        @Schema(description = "서버에 업로드된 파일 제목")
        String uploadFileName,
        @Schema(description = "서버에 업로드된 파일 경로")
        String uploadFileURL,
        @Schema(description = "서버에 업로드된 파일 확장자", example = "PNG, JPG")
        FileType fileType
) {
    @Builder
    public PostFileResponse(String uploadFileName, String uploadFileURL, FileType fileType) {
        this.uploadFileName = uploadFileName;
        this.uploadFileURL = uploadFileURL;
        this.fileType = fileType;
    }
}
