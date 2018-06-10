package com.herbinm.edx.captureme.gateway.facade;

import com.herbinm.edx.captureme.gateway.domain.Photo;
import com.herbinm.edx.captureme.gateway.domain.User;
import com.herbinm.edx.captureme.gateway.facade.data.PhotoData;
import com.herbinm.edx.captureme.gateway.service.recognition.ImageRecognition;
import com.herbinm.edx.captureme.gateway.service.storage.PhotoDetailsStorage;
import com.herbinm.edx.captureme.gateway.service.storage.PhotoStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;

import static com.herbinm.edx.captureme.gateway.domain.Photo.aPhoto;
import static com.herbinm.edx.captureme.gateway.facade.data.PhotoData.photoData;
import static java.util.stream.Collectors.toList;

@Service
public class DefaultPhotosFacade implements PhotosFacade {

    private final PhotoStorage photoStorage;
    private final PhotoDetailsStorage photoDetailsStorage;
    private final ImageRecognition imageRecognition;

    @Inject
    public DefaultPhotosFacade(PhotoStorage photoStorage, PhotoDetailsStorage photoDetailsStorage, ImageRecognition imageRecognition) {
        this.photoStorage = photoStorage;
        this.photoDetailsStorage = photoDetailsStorage;
        this.imageRecognition = imageRecognition;
    }

    @Override
    public PhotoData uploadPhoto(MultipartFile photoFile, User user) {
        String objectKey = photoStorage.uploadImage(photoFile);
        List<String> labels = imageRecognition.labels(objectKey);
        photoDetailsStorage.save(aPhoto(objectKey).labels(labels).build(), user.getUniqueId());
        return photoData().objectKey(objectKey).labels(labels).build();
    }

    @Override
    public List<PhotoData> findAllPhotos(User user) {
        List<Photo> allPhotos = photoDetailsStorage.allPhotos(user.getUniqueId());
        return allPhotos.stream().map(
                photo -> {
                    URL accessUrl = photoStorage.imageUrl(photo.getObjectKey());
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
    public PhotoData findPhotoByKey(String objectKey) {
        Photo photo = photoDetailsStorage.load(objectKey);
        URL accessUrl = photoStorage.imageUrl(objectKey);
        return photoData()
                .objectKey(photo.getObjectKey())
                .labels(photo.getLabels())
                .accessUrl(accessUrl)
                .uploadedAt(photo.getUploadedAt())
                .build();
    }

}
