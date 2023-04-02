package com.girigiri.kwrental.aws;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.girigiri.kwrental.common.MultiPartFileHandler;
import com.girigiri.kwrental.common.exception.EmptyMultiPartFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

@Service
public class AwsS3Service implements MultiPartFileHandler {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public AwsS3Service(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    private static ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }

    private static String getFileExtension(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("파일이름 없음");
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    @Override
    public URL upload(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new EmptyMultiPartFileException();
        }
        ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);
        String fileName = createFileName(multipartFile);
        amazonS3.putObject(bucketName, fileName, multipartFile.getInputStream(), objectMetadata);
        return amazonS3.getUrl(bucketName, fileName);
    }

    private String createFileName(final MultipartFile multipartFile) {
        String fileExtension = getFileExtension(multipartFile);
        return UUID.randomUUID() + fileExtension;
    }
}
