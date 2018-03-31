package com.herbinm.edx.captureme.gateway.service.recognition;

import java.util.List;

public interface ImageRecognition {

    List<String> labels(String imageKey);

}
