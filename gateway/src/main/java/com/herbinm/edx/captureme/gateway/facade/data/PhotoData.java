package com.herbinm.edx.captureme.gateway.facade.data;

import java.net.URL;
import java.util.List;

public class PhotoData {

    private final String objectKey;
    private final URL accessUrl;
    private final List<String> labels;

    private PhotoData(String objectKey, URL accessUrl, List<String> labels) {
        this.objectKey = objectKey;
        this.accessUrl = accessUrl;
        this.labels = labels;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public URL getAccessUrl() {
        return accessUrl;
    }

    public List<String> getLabels() {
        return labels;
    }

    public static PhotoDataBuilder photoData() {
        return new PhotoDataBuilder();
    }

    public static class PhotoDataBuilder {

        private String objectKey;
        private List<String> labels;
        private URL accessUrl;

        private PhotoDataBuilder() {
        }

        public PhotoDataBuilder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public PhotoDataBuilder labels(List<String> labels) {
            this.labels = labels;
            return this;
        }

        public PhotoDataBuilder accessUrl(URL accessUrl) {
            this.accessUrl = accessUrl;
            return this;
        }

        public PhotoData build() {
            return new PhotoData(objectKey, accessUrl, labels);
        }

    }

}
