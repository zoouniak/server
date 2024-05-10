package com.example.cns.chat.converter;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MultipartFileConverter implements MultipartFile {
    private final byte[] imgBytes;

    public MultipartFileConverter(byte[] imgBytes) {
        this.imgBytes = imgBytes;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getOriginalFilename() {
        return generateRandomFileName();
    }

    @Override
    public String getContentType() {
        return "image/png";
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

    private String generateRandomFileName() {
        // UUID 생성
        UUID uuid = UUID.randomUUID();

        // UUID를 문자열로 변환하고 '-'를 제거하여 파일 이름에 사용합니다.
        String fileName = uuid.toString().replace("-", "");

        return fileName + ".png";
    }
}
