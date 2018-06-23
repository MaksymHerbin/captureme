package com.herbinm.edx.captureme.gateway.photos.domain;

import com.google.common.collect.ImmutableList;

import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;

public class Photo {

    private final String objectKey;
    private final String s3ObjectKey;
    private final List<String> labels;
    private final Date uploadedAt;
    private final String userId;

    private Photo(String objectKey, String s3ObjectKey, List<String> labels, Date uploadedAt, String userId) {
        this.objectKey = objectKey;
        this.s3ObjectKey = s3ObjectKey;
        this.userId = userId;

        if (uploadedAt != null) {
            this.uploadedAt = new Date(uploadedAt.getTime());
        } else {
            this.uploadedAt = null;
        }

        if (labels != null) {
            this.labels = ImmutableList.copyOf(labels);
        } else {
            this.labels = ImmutableList.copyOf(emptyList());
        }
    }

    public static PhotoBuilder aPhoto(String objectKey, String s3ObjectKey) {
        checkNotNull(objectKey, "Photo must have an objectKey specified");
        checkNotNull(s3ObjectKey, "Photo must have an s3ObjectKey specified");
        return new PhotoBuilder(objectKey, s3ObjectKey);
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getS3ObjectKey() {
        return s3ObjectKey;
    }

    public List<String> getLabels() {
        return labels;
    }

    public Date getUploadedAt() {
        return uploadedAt != null ? new Date(uploadedAt.getTime()) : null;
    }

    public String getUserId() {
        return userId;
    }

    public static class PhotoBuilder {

        private String objectKey;
        private String s3ObjectKey;
        private List<String> labels;
        private Date uploadedAt;
        private String userId;

        private PhotoBuilder(String objectKey, String s3ObjectKey) {
            this.objectKey = objectKey;
            this.s3ObjectKey = s3ObjectKey;
        }

        public PhotoBuilder labels(List<String> labels) {
            this.labels = labels;
            return this;
        }

        public PhotoBuilder uploadedAt(Date uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public PhotoBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Photo build() {
            return new Photo(objectKey, s3ObjectKey, labels, uploadedAt, userId);
        }

    }

}
