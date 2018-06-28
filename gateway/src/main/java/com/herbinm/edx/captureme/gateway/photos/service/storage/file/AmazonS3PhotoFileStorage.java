package com.herbinm.edx.captureme.gateway.photos.service.storage.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.xray.spring.aop.XRayEnabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.sync.RequestBody;

import javax.inject.Inject;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.joda.time.DateTime.now;

@Service
@XRayEnabled
public class AmazonS3PhotoFileStorage implements PhotoFileStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3PhotoFileStorage.class);

    private final AmazonS3 awsS3ClientV1;
    private final S3Client awsS3ClientV2;

    private final String bucketName;
    private final String prefix;

    @Inject
    public AmazonS3PhotoFileStorage(AmazonS3 awsS3ClientV1,
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
    public String uploadImage(MultipartFile multipartFile, String objectKey) {
        try {
            String s3ObjectKey = prefix + objectKey + ".png";
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3ObjectKey)
                    .build();
            awsS3ClientV2.putObject(putObjectRequest, RequestBody.of(multipartFile.getBytes()));
            return s3ObjectKey;
        } catch (Exception exception) {
            LOGGER.error("Exception while loading  {} to S3", multipartFile.getOriginalFilename(), exception);
            return null;
        }
    }

    @Override
    public void delete(String s3ObjectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3ObjectKey)
                .build();
        awsS3ClientV2.deleteObject(deleteObjectRequest);
    }

    @Override
    public URL imageUrl(String s3ObjectKey) {
        if (s3ObjectKey != null) {
            return getPhotoUrl(s3ObjectKey);
        }
        LOGGER.debug("No image key provided for getting URL");
        return null;
    }

    private URL getPhotoUrl(String key) {
        try {
            return awsS3ClientV1.generatePresignedUrl(bucketName, key, now().plusHours(1).toDate());
        } catch (Exception exception) {
            LOGGER.error("Exception while file url for key {}", key, exception);
        }
        return null;
    }

}
