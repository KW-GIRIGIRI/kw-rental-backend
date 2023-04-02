package com.girigiri.kwrental.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.girigiri.kwrental.testsupport.MockAmazonS3Builder;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class AwsS3ServiceTest {

    private static S3Mock S3;
    private static AmazonS3 amazonS3;
    private AwsS3Service awsS3Service;

    @BeforeAll
    static void beforeAll() {
        S3 = new S3Mock.Builder()
                .withPort(8001)
                .withInMemoryBackend()
                .build();
        S3.start();

        amazonS3 = MockAmazonS3Builder.create(8001, Regions.AP_NORTHEAST_2);
        amazonS3.createBucket("bucket");
    }

    @AfterAll
    static void afterAll() {
        S3.shutdown();
    }

    @BeforeEach
    void setUp() {
        awsS3Service = new AwsS3Service(amazonS3, "bucket");
    }

    @Test
    void uploadImage() throws IOException {
        // given
        String path = "test.png";
        String contentType = "image/png";

        MockMultipartFile file = new MockMultipartFile("test", path, contentType, "test".getBytes());

        // when
        URL url = awsS3Service.upload(file);

        // then
        assertThat(url.toString()).contains(".png");
    }
}