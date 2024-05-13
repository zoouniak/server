package com.example.cns.chat.converter;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFileConverter implements MultipartFile {
    private final byte[] imgBytes;
    private final String originalFileName;
    private final String extension;

    public MultipartFileConverter(byte[] imgBytes, String originalFileName, String extension) {
        this.imgBytes = imgBytes;
        this.originalFileName = originalFileName;
        this.extension = extension;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getOriginalFilename() {
        return originalFileName;
    }

    @Override
    public String getContentType() {
        return switch (extension) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpg";
            case "pdf" -> "application/pdf";
            case "ppt" -> "application/vnd.ms-powerpoint";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "doc" -> "application/msword";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> throw new BusinessException(ExceptionCode.FILE_NOT_SUPPORT);
        };
    }

    @Override
    public boolean isEmpty() {
        return imgBytes == null || imgBytes.length == 0;
    }

    @Override
    public long getSize() {
        return imgBytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return imgBytes;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(imgBytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {

    }

}
