package com.herbinm.edx.captureme.gateway.photos.service.storage.file;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface PhotoStorage {

    String uploadImage(MultipartFile multipartFile);

    URL imageUrl(String objectKey);

}
