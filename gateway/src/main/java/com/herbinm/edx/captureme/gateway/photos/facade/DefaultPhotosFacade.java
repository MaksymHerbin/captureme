package com.herbinm.edx.captureme.gateway.photos.facade;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.herbinm.edx.captureme.gateway.domain.User;
import com.herbinm.edx.captureme.gateway.photos.domain.Photo;
import com.herbinm.edx.captureme.gateway.photos.facade.data.PhotoData;
import com.herbinm.edx.captureme.gateway.photos.service.recognition.ImageRecognition;
import com.herbinm.edx.captureme.gateway.photos.service.storage.file.PhotoFileStorage;
import com.herbinm.edx.captureme.gateway.photos.service.storage.info.PhotoDetailsStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;

import static com.herbinm.edx.captureme.gateway.photos.domain.Photo.aPhoto;
import static com.herbinm.edx.captureme.gateway.photos.facade.data.PhotoData.photoData;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

@Service
@XRayEnabled
public class DefaultPhotosFacade implements PhotosFacade {

    private final PhotoFileStorage photoFileStorage;
    private final PhotoDetailsStorage photoDetailsStorage;
    private final ImageRecognition imageRecognition;

    @Inject
    public DefaultPhotosFacade(PhotoFileStorage photoFileStorage, PhotoDetailsStorage photoDetailsStorage, ImageRecognition imageRecognition) {
        this.photoFileStorage = photoFileStorage;
        this.photoDetailsStorage = photoDetailsStorage;
        this.imageRecognition = imageRecognition;
    }

    @Override
    public PhotoData uploadPhoto(MultipartFile photoFile, User user) {
        String objectKey = randomUUID().toString();
        String s3ObjectKey = photoFileStorage.uploadImage(photoFile, objectKey);
        List<String> labels = imageRecognition.labels(s3ObjectKey);
        photoDetailsStorage.save(aPhoto(objectKey, s3ObjectKey).labels(labels).build(), user.getUniqueId());
        return photoData().objectKey(objectKey).labels(labels).build();
    }

    @Override
    public List<PhotoData> findAllPhotos(User user) {
        List<Photo> allPhotos = photoDetailsStorage.allPhotos(user.getUniqueId());
        return allPhotos.stream().map(
                photo -> {
                    URL accessUrl = photoFileStorage.imageUrl(photo.getS3ObjectKey());
                    return photoData()
                            .objectKey(photo.getObjectKey())
                            .labels(photo.getLabels())
                            .accessUrl(accessUrl)
                            .uploadedAt(photo.getUploadedAt())
                            .build();
                }
        ).collect(toList());
    }

    @Override
    public void delete(String photoId, User user) {
        Photo toDelete = photoDetailsStorage.loadPhoto(photoId);
        if (toDelete.getUserId().equals(user.getUniqueId())) {
            photoFileStorage.delete(toDelete.getS3ObjectKey());
            photoDetailsStorage.delete(photoId, user.getUniqueId());
        }
    }

}
