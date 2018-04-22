package com.herbinm.edx.captureme.gateway.service.storage;

import com.herbinm.edx.captureme.gateway.domain.Photo;

import java.util.List;

public interface PhotoDetailsStorage {

    void save(Photo photo);

    Photo load(String objectKey);

    List<Photo> allPhotos();

}