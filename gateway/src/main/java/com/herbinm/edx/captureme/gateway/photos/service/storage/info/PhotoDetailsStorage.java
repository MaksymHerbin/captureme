package com.herbinm.edx.captureme.gateway.photos.service.storage.info;

import com.herbinm.edx.captureme.gateway.photos.domain.Photo;

import java.util.List;

public interface PhotoDetailsStorage {

    void save(Photo photo, String userName);

    Photo load(String objectKey);

    List<Photo> allPhotos(String userId);

}
