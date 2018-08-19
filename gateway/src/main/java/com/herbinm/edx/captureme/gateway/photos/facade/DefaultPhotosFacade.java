package com.herbinm.edx.captureme.gateway.photos.facade;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.herbinm.edx.captureme.gateway.domain.User;
import com.herbinm.edx.captureme.gateway.photos.domain.Photo;
import com.herbinm.edx.captureme.gateway.photos.facade.data.PhotoData;
import com.herbinm.edx.captureme.gateway.photos.service.storage.file.PhotoFileStorage;
import com.herbinm.edx.captureme.gateway.photos.service.storage.info.PhotoDetailsStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.Collections;

import static com.herbinm.edx.captureme.gateway.photos.domain.Photo.aPhoto;
import static com.herbinm.edx.captureme.gateway.photos.facade.data.PhotoData.photoData;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

@Service
@XRayEnabled
public class DefaultPhotosFacade implements PhotosFacade {

    private final PhotoFileStorage photoFileStorage;
    private final PhotoDetailsStorage photoDetailsStorage;

    @Inject
    public DefaultPhotosFacade(PhotoFileStorage photoFileStorage, PhotoDetailsStorage photoDetailsStorage) {
        this.photoFileStorage = photoFileStorage;
        this.photoDetailsStorage = photoDetailsStorage;
    }

    @Override
    public PhotoData uploadPhoto(MultipartFile photoFile, User user) {
        String objectKey = randomUUID().toString();
        String s3ObjectKey = photoFileStorage.uploadPhotoFile(photoFile, objectKey);
        photoDetailsStorage.savePhotoDetails(aPhoto(objectKey, s3ObjectKey).build(), user.getUniqueId());
        return photoData().objectKey(objectKey).build();
    }

    @Override
    public List<PhotoData> findAllPhotos(User user) {
        List<Photo> allPhotos = photoDetailsStorage.getAllPhotosForUser(user.getUniqueId());
        return allPhotos.stream().map(
                photo -> {
                    URL accessUrl = photoFileStorage.getPhotoFileUrl(photo.getS3ObjectKey());
                    return photoData()
                            .objectKey(photo.getObjectKey())
                            .labels(photo.getLabels().isEmpty() ? Collections.singletonList("Labels will be awailable soon......") : photo.getLabels())
                            .accessUrl(accessUrl)
                            .uploadedAt(photo.getUploadedAt())
                            .build();
                }
        ).collect(toList());
    }

    @Override
    public void deletePhoto(String photoId, User user) {
        Photo toDelete = photoDetailsStorage.loadPhotoDetails(photoId);
        if (toDelete.getUserId().equals(user.getUniqueId())) {
            photoFileStorage.deletePhotoFile(toDelete.getS3ObjectKey());
            photoDetailsStorage.deletePhotoDetails(photoId, user.getUniqueId());
        }
    }

}
