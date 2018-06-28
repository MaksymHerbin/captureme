package com.herbinm.edx.captureme.gateway.photos.facade;

import com.herbinm.edx.captureme.gateway.domain.User;
import com.herbinm.edx.captureme.gateway.photos.facade.data.PhotoData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotosFacade {

    PhotoData uploadPhoto(MultipartFile photoFile, User user);

    List<PhotoData> findAllPhotos(User user);

    void deletePhoto(String photoId, User user);

}
