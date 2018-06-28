package com.herbinm.edx.captureme.gateway.photos.service.recognition;

import java.util.List;

public interface ImageRecognition {

    List<String> getPhotoLabels(String imageKey);

}
