package com.example.cns.common.service;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;
import com.example.cns.common.type.FileType;
import com.example.cns.feed.post.dto.response.FileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    /*
    게시글 파일 업로드
    1. post 폴더에 저장하는 방식
    2. 파일을 가져오고 UUID형태로 바꾸고 저장
    3. 해당 파일 경로 제공
     */
    public List<FileResponse> uploadFileList(List<MultipartFile> postFileRequest, String path) {
         /*
        각 multipart를 file로 변경후 객체로 바꾸어 S3에 저장
         */
        return postFileRequest.stream()
                .map(file -> uploadFile(file, path)) // 각 MultipartFile을 uploadFile 함수에 전달하여 FileResponse로 변환
                .collect(Collectors.toList());
    }

    public FileResponse uploadFile(MultipartFile multipartFile, String path) {
        FileType fileType = null;

        String ext = extractType(multipartFile.getOriginalFilename());

        switch (ext) {
            case "png", "PNG" -> fileType = FileType.PNG;
            case "jpg", "JPG", "jpeg", "JPEG" -> fileType = FileType.JPG;
            default -> throw new BusinessException(ExceptionCode.NOT_SUPPORT_EXT);
        }

        String fileName = multipartFile.getOriginalFilename();
        String uploadFileName = getUUIDFileName(fileName);
        String uploadFileURL;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            String keyName = path + "/" + uploadFileName;

            //S3에 저장하는데 외부에서 읽기 가능 권한 부여해서 저장
            amazonS3Client.putObject(
                    new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );

            uploadFileURL = amazonS3Client.getUrl(bucketName, keyName).toString();
            return new FileResponse(uploadFileName, uploadFileURL, fileType);
        } catch (IOException e) {
            throw new BusinessException(ExceptionCode.IMAGE_UPLOAD_FAILED);
        }
    }

    public void deleteFile(String fileName, String path) throws IOException {
        try {
            String keyName = path + "/" + fileName;
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
        } catch (SdkClientException e) {
            throw new BusinessException(ExceptionCode.IMAGE_DELETE_FAILED);
        }
    }

    private String getUUIDFileName(String fileName) {
        String ext = extractType(fileName);
        return UUID.randomUUID() + "." + ext;
    }

    private String extractType(String fileName) {
        return fileName.substring(fileName.indexOf(".") + 1);
    }

}
