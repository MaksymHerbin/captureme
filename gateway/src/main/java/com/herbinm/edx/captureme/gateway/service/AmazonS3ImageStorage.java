package com.herbinm.edx.captureme.gateway.service;

import com.amazonaws.services.s3.AmazonS3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.sync.RequestBody;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptySet;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.joda.time.DateTime.now;

@Service
public class AmazonS3ImageStorage implements ImageStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3ImageStorage.class);

    private final AmazonS3 awsS3ClientV1;
    private final S3Client awsS3ClientV2;

    private final String bucketName;
    private final String prefix;

    @Inject
    public AmazonS3ImageStorage(AmazonS3 awsS3ClientV1,
                                S3Client awsS3ClientV2,
                                @Value("${aws.s3.photos.bucket}") String bucketName,
                                @Value("${aws.s3.photos.prefix}") String prefix) {
        this.awsS3ClientV1 = awsS3ClientV1;
        this.awsS3ClientV2 = awsS3ClientV2;
        this.bucketName = bucketName;
        this.prefix = prefix;
        checkNotNull(this.bucketName, "Bucket name for photos must be specified");
        checkNotNull(this.prefix, "Prefix must be specified");
    }

    @Override
    public void saveImage(MultipartFile multipartFile) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(prefix + randomUUID().toString() + ".png")
                    .build();
            awsS3ClientV2.putObject(putObjectRequest, RequestBody.of(multipartFile.getBytes()));
        } catch (Exception exception) {
            LOGGER.error("Exception while loading  {} to S3", multipartFile.getOriginalFilename(), exception);
        }
    }

    @Override
    public List<URL> allImagesUrls() {
        return obtainPhotosKeys().stream().map(this::getPhotoUrl).collect(toList());
    }

    private URL getPhotoUrl(String key) {
        try {
            return awsS3ClientV1.generatePresignedUrl(bucketName, key, now().plusHours(1).toDate());
        } catch (Exception exception) {
            LOGGER.error("Exception while file url for key {}", key, exception);
        }
        return null;
    }

    private Set<String> obtainPhotosKeys() {
        try {
            ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build();
            return awsS3ClientV2.listObjects(listObjectsRequest).contents().stream().map(S3Object::key).collect(toSet());
        } catch (Exception exception) {
            LOGGER.error("Exception while getting list of images from to S3", exception);
        }
        return emptySet();
    }
}
