package com.herbinm.edx.captureme.gateway.photos.service.storage.info;

import com.herbinm.edx.captureme.gateway.photos.domain.Photo;

import java.util.List;

public interface PhotoDetailsStorage {

    void save(Photo photo, String userName);

    void delete(String objectKey, String userId);

    Photo loadPhoto(String photoId);

    List<Photo> allPhotos(String userId);

}
