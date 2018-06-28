package com.herbinm.edx.captureme.gateway.photos.service.storage.info;

import com.herbinm.edx.captureme.gateway.photos.domain.Photo;

import java.util.List;

public interface PhotoDetailsStorage {

    void savePhotoDetails(Photo photo, String userName);

    void deletePhotoDetails(String objectKey, String userId);

    Photo loadPhotoDetails(String photoId);

    List<Photo> getAllPhotosForUser(String userId);

}
