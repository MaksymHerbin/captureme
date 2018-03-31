package com.herbinm.edx.captureme.gateway.service.recognition;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

@Service
public class AmazonImageRecognition implements ImageRecognition {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonImageRecognition.class);


    private final AmazonRekognition awsRecognitionClient;

    private final String bucketName;
    private final int maximumLabels;
    private final float minimumConfidence;

    @Inject
    public AmazonImageRecognition(AmazonRekognition awsRecognitionClient,
                                  @Value("${aws.s3.photos.bucket}") String bucketName,
                                  @Value("${aws.recognition.maximum_labels:10}") int maximumLabels,
                                  @Value("${aws.recognition.minimum_confidence:80}") float minimumConfidence) {
        this.awsRecognitionClient = awsRecognitionClient;
        this.bucketName = bucketName;
        this.maximumLabels = maximumLabels;
        this.minimumConfidence = minimumConfidence;
        checkNotNull(bucketName);
    }

    @Override
    public List<String> labels(String imageKey) {
        LOGGER.trace("Searching for labels for image in S3 with key {}", imageKey);
        S3Object s3Image = new S3Object().withName(imageKey).withBucket(bucketName);
        Image image = new Image().withS3Object(s3Image);

        DetectLabelsRequest request = new DetectLabelsRequest()
                .withImage(image).withMaxLabels(maximumLabels).withMinConfidence(minimumConfidence);

        DetectLabelsResult result = awsRecognitionClient.detectLabels(request);
        return result.getLabels().stream().map(Label::getName).collect(toList());
    }


}
