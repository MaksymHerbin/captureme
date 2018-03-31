package com.herbinm.edx.captureme.gateway.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

public interface ImageStorage {

    String saveImage(MultipartFile multipartFile);

    URL imageUrl(String imageKey);

    List<URL> allImagesUrls();


}
