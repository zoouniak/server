package com.example.cns.common.service;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.type.FileType;
import com.example.cns.feed.post.dto.request.PostFileRequest;
import com.example.cns.feed.post.dto.response.PostFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.s3.folder2}")
    private String postPath;

    /*
    게시글 파일 업로드
    1. post 폴더에 저장하는 방식
    2. 파일을 가져오고 UUID형태로 바꾸고 저장
    3. 해당 파일 경로 제공
     */
    public List<PostFileResponse> uploadPostFile(List<MultipartFile> postFileRequest){
        List<PostFileResponse> uploadFileList = new ArrayList<>();

        /*
        각 multipart를 file로 변경후 객체로 바꾸어 S3에 저장
         */

        if(postFileRequest.size()>=1){
            postFileRequest.forEach(
                    multipartFile -> {

                        FileType fileType = null;

                        String ext = extractType(multipartFile.getOriginalFilename());

                        switch (ext) {
                            case "png", "PNG" -> fileType = FileType.PNG;
                            case "jpg", "JPG", "jpeg", "JPEG" -> fileType = FileType.JPG;
                            default -> {
                                //throw new BusinessException();
                            }
                        }

                        String fileName = multipartFile.getOriginalFilename();
                        String uploadFileName = getUUIDFileName(fileName);
                        String uploadFileURL;

                        ObjectMetadata objectMetadata = new ObjectMetadata();
                        objectMetadata.setContentLength(multipartFile.getSize());
                        objectMetadata.setContentType(multipartFile.getContentType());

                        try(InputStream inputStream = multipartFile.getInputStream()){
                            String keyName = postPath +"/"+ uploadFileName;

                            //S3에 저장하는데 외부에서 읽기 가능 권한 부여해서 저장
                            amazonS3Client.putObject(
                                    new PutObjectRequest(bucketName,keyName,inputStream,objectMetadata)
                                            .withCannedAcl(CannedAccessControlList.PublicRead)
                            );

                            uploadFileURL = amazonS3Client.getUrl(bucketName, keyName).toString();
                            uploadFileList.add(new PostFileResponse(uploadFileName,uploadFileURL,fileType));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        }

        return uploadFileList;
    }

    public void deleteFile(String fileName) throws IOException{
        try{
            String keyName = postPath + "/" + fileName;
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName,keyName));
        }catch (SdkClientException e){
            throw new IOException("S3에서 삭제 실패",e);
        }
    }

    private String getUUIDFileName(String fileName) {
        String ext = extractType(fileName);
        return UUID.randomUUID() + "." + ext;
    }

    private String extractType(String fileName){
        return fileName.substring(fileName.indexOf(".")+1);
    }

}
