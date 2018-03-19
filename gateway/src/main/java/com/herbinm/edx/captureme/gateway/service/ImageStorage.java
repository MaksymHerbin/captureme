package com.herbinm.edx.captureme.gateway.service;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

public interface ImageStorage {

    void saveImage(MultipartFile multipartFile);

    List<URL> allImagesUrls();


}
