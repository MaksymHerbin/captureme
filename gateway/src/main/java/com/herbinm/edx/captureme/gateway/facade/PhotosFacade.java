package com.herbinm.edx.captureme.gateway.facade;

import com.herbinm.edx.captureme.gateway.domain.User;
import com.herbinm.edx.captureme.gateway.facade.data.PhotoData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotosFacade {

    PhotoData uploadPhoto(MultipartFile photoFile, User user);

    List<PhotoData> findAllPhotos(User user);

    PhotoData findPhotoByKey(String objectKey);

}
