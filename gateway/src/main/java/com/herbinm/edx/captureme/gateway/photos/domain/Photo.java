package com.herbinm.edx.captureme.gateway.photos.domain;

import com.google.common.collect.ImmutableList;

import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;

public class Photo {

    private final String objectKey;
    private final List<String> labels;
    private final Date uploadedAt;

    private Photo(String objectKey, List<String> labels, Date uploadedAt) {
        this.objectKey = objectKey;

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

    public static PhotoBuilder aPhoto(String objectKey) {
        checkNotNull(objectKey, "Photo must have an objectKey specified");
        return new PhotoBuilder(objectKey);
    }

    public String getObjectKey() {
        return objectKey;
    }

    public List<String> getLabels() {
        return labels;
    }

    public Date getUploadedAt() {
        return uploadedAt != null ? new Date(uploadedAt.getTime()) : null;
    }

    public static class PhotoBuilder {

        private String objectKey;
        private List<String> labels;
        private Date uploadedAt;

        private PhotoBuilder(String objectKey) {
            this.objectKey = objectKey;
        }

        public PhotoBuilder labels(List<String> labels) {
            this.labels = labels;
            return this;
        }

        public PhotoBuilder uploadedAt(Date uploadedAt) {
            this.uploadedAt = uploadedAt;
            return this;
        }

        public Photo build() {
            return new Photo(objectKey, labels, uploadedAt);
        }

    }

}
