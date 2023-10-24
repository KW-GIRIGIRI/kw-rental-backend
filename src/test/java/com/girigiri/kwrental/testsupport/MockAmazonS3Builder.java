package com.girigiri.kwrental.testsupport;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class MockAmazonS3Builder {

    public static AmazonS3 create(final int port, Regions region) {
        AwsClientBuilder.EndpointConfiguration endpoint
                = new AwsClientBuilder.EndpointConfiguration("http://localhost:" + port, region.getName());
        AWSStaticCredentialsProvider credentialsProvider
                = new AWSStaticCredentialsProvider(new AnonymousAWSCredentials());
        return AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(credentialsProvider)
                .build();
    }
}
