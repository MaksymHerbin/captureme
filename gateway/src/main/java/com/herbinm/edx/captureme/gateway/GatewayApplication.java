package com.herbinm.edx.captureme.gateway;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.herbinm.edx.captureme.gateway.service.authentification.CognitoClientProperties;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // This instance is needed because, new version of S3Client does not support some opperations
    // In case operation is present in new version of SDK, it should be used
    @Bean
    public AmazonS3 awsS3ClientV1() {
        return AmazonS3Client.builder().
                withCredentials(new DefaultAWSCredentialsProviderChain()).
                withRegion("us-west-2").build();
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
                .region(Region.US_WEST_2).build();
    }

    @Bean
    public AmazonRekognition awsRecognitionClient() {
        return AmazonRekognitionClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
    }

    @Bean
    public JWTProcessor jwtProcessor(CognitoClientProperties cognitoClientProperties) throws MalformedURLException {
        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource keySource = new RemoteJWKSet(
                new URL(buildJWKSourceUrl(cognitoClientProperties)),
                new DefaultResourceRetriever(3600, 3600)
        );
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
        return jwtProcessor;
    }

    @Bean
    public CognitoClientProperties cognitoClientProperties(
            @Value("${cognito.client.id}") String clientId,
            @Value("${cognito.domain}") String domain,
            @Value("${cognito.poolId}") String poolId,
            @Value("${cognito.client.secret}") String clientSecret
    ) {
        return new CognitoClientProperties(domain, poolId, clientId, clientSecret);
    }

    private String buildJWKSourceUrl(CognitoClientProperties cognitoClientProperties) {
        return String.format(
                "https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json",
                Regions.US_WEST_2.getName(),
                cognitoClientProperties.getPoolId()
        );
    }

}
