package com.herbinm.edx.captureme.gateway.photos.service.storage.file;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface PhotoFileStorage {

    String uploadImage(MultipartFile multipartFile, String objectKey);

    void delete(String objectKey);

    URL imageUrl(String objectKey);

}
