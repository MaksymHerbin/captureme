package com.herbinm.edx.captureme.gateway.photos.service.storage.file;

import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface PhotoFileStorage {

    String uploadPhotoFile(MultipartFile multipartFile, String objectKey);

    void deletePhotoFile(String objectKey);

    URL getPhotoFileUrl(String objectKey);

}
