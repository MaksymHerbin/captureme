package com.herbinm.edx.captureme.gateway.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;

public interface PhotoStorage {

    String uploadImage(MultipartFile multipartFile);

    URL imageUrl(String objectKey);

    List<URL> allImagesUrls();


}
