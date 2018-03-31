package com.herbinm.edx.captureme.gateway;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // This instance is needed because, new version of S3Client does not support some opperations
    // In case operation is present in new version of SDK, it should be used
    @Bean
    public AmazonS3 awsS3ClientV1(EnvironmentVariableCredentialsProvider credentialsProvider) {
        return AmazonS3Client.builder()
                .withRegion("us-west-2")
                .withCredentials(credentialsProvider)
                .build();
    }

    @Bean
    public S3Client awsS3ClientV2() {
        return S3Client.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(new software.amazon.awssdk.auth.EnvironmentVariableCredentialsProvider())
                .build();
    }

    @Bean
    public AmazonRekognition awsRecognitionClient(EnvironmentVariableCredentialsProvider credentialsProvider) {
        return AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(credentialsProvider)
                .build();
    }

    @Bean
    public EnvironmentVariableCredentialsProvider credentialsProvider() {
        return new EnvironmentVariableCredentialsProvider();
    }

}
