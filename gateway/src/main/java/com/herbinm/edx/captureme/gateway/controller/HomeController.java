package com.herbinm.edx.captureme.gateway.controller;

import com.amazonaws.services.s3.AmazonS3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.sync.RequestBody;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.joda.time.DateTime.now;

@Controller
public class HomeController {

    private static final String BUCKET_NAME = "edx-images-bucket";
    private static final String PREFIX = "photos/";

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Inject
    private AmazonS3 awsS3ClientV1;

    @Inject
    private S3Client awsS3ClientV2;

    @GetMapping("/")
    public String home(Model model) {
        LOGGER.info("Listing all photos available");
        List<String> photosUrls = obtainPhotosKeys().stream().map(this::getPhotoUrl).collect(toList());
        model.addAttribute("photos", photosUrls);
        return "main";
    }

    private String getPhotoUrl(String key) {
        try {
            return awsS3ClientV1.generatePresignedUrl(BUCKET_NAME, key, now().plusHours(1).toDate()).toString();
        } catch (Exception exception) {
            LOGGER.error("Exception while file url for key {}", key, exception);
        }
        return null;
    }

    private Set<String> obtainPhotosKeys() {
        try {
            ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(PREFIX)
                    .build();
            ListObjectsResponse listObjectsResponse = awsS3ClientV2.listObjects(listObjectsRequest);
            return listObjectsResponse.contents().stream().map(S3Object::key).collect(toSet());
        } catch (Exception exception) {
            LOGGER.error("Exception while getting list of images from to S3", exception);
        }
        return emptySet();
    }

    @PostMapping("/")
    public String uploadPhoto(@RequestParam("photo") MultipartFile multipartFile) {
        LOGGER.info("Uploading photo {}, size {}", multipartFile.getOriginalFilename(), multipartFile.getSize());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(PREFIX + randomUUID().toString() + ".png")
                    .build();
            awsS3ClientV2.putObject(putObjectRequest, RequestBody.of(multipartFile.getBytes()));
        } catch (Exception exception) {
            LOGGER.error("Exception while loading  {} to S3", multipartFile.getOriginalFilename(), exception);
        }

        return "redirect:/";
    }

}
