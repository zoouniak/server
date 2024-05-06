package com.example.cns.feed.post.dto.response;

import com.example.cns.common.type.FileType;

public record PostFileResponse(
        String uploadFileName,
        String uploadFileURL,
        FileType fileType
) {
}
