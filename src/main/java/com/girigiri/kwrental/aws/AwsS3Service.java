package com.girigiri.kwrental.aws;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.girigiri.kwrental.aws.exception.FileNameEmptyException;
import com.girigiri.kwrental.common.MultiPartFileHandler;
import com.girigiri.kwrental.common.exception.EmptyMultiPartFileException;

@Service
public class AwsS3Service implements MultiPartFileHandler {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public AwsS3Service(final AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") final String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    @Override
    public URL upload(final MultipartFile multipartFile) throws IOException {
        validateEmptyMultipartFile(multipartFile);
        ObjectMetadata objectMetadata = createObjectMetadata(multipartFile);
        String fileName = createFileName(multipartFile);
        amazonS3.putObject(bucketName, fileName, multipartFile.getInputStream(), objectMetadata);
        return amazonS3.getUrl(bucketName, fileName);
    }

    private void validateEmptyMultipartFile(final MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new EmptyMultiPartFileException();
        }
    }

    private ObjectMetadata createObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }

    private String createFileName(final MultipartFile multipartFile) {
        String fileExtension = getFileExtension(multipartFile);
        return UUID.randomUUID() + fileExtension;
    }

    private String getFileExtension(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileNameEmptyException();
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}
