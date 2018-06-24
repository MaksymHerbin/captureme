package com.herbinm.edx.captureme.gateway.photos;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class PhotosConfiguration {

    // This instance is needed because, new version of S3Client does not support some opperations
    // In case operation is present in new version of SDK, it should be used
    @Bean
    public AmazonS3 awsS3ClientV1() {
        return AmazonS3Client.builder()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
                .withRegion(Regions.US_WEST_2)
                .build();
    }

    @Bean
    public S3Client awsS3ClientV2() {
        return S3Client.builder()
                .credentialsProvider(AwsCredentialsProviderChain
                        .builder()
                        .addCredentialsProvider(new EnvironmentVariableCredentialsProvider())
                        .addCredentialsProvider(InstanceProfileCredentialsProvider.builder().build())
                        .build()
                )
                .region(Region.US_WEST_2)
                .build();
    }

    @Bean
    public AmazonRekognition awsRecognitionClient() {
        return AmazonRekognitionClientBuilder.standard()
                .withRequestHandlers(new TracingHandler(AWSXRay.getGlobalRecorder()))
                .withRegion(Regions.US_WEST_2).build();
    }

}
