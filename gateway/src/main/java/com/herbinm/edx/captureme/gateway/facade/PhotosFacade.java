package com.herbinm.edx.captureme.gateway.facade;

import com.herbinm.edx.captureme.gateway.facade.data.PhotoData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotosFacade {

    PhotoData uploadPhoto(MultipartFile photoFile);

    List<PhotoData> findAllPhotos();

    PhotoData findPhotoByKey(String objectKey);

}