package com.herbinm.edx.captureme.gateway.facade;

import com.herbinm.edx.captureme.gateway.facade.data.PhotoData;
import com.herbinm.edx.captureme.gateway.service.recognition.ImageRecognition;
import com.herbinm.edx.captureme.gateway.service.storage.PhotoStorage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;

import static com.herbinm.edx.captureme.gateway.facade.data.PhotoData.photoData;
import static java.util.stream.Collectors.toList;

@Service
public class DefaultPhotosFacade implements PhotosFacade {

    private final PhotoStorage photoStorage;
    private final ImageRecognition imageRecognition;

    @Inject
    public DefaultPhotosFacade(PhotoStorage photoStorage, ImageRecognition imageRecognition) {
        this.photoStorage = photoStorage;
        this.imageRecognition = imageRecognition;
    }

    @Override
    public PhotoData uploadPhoto(MultipartFile photoFile) {
        String objectKey = photoStorage.uploadImage(photoFile);
        return photoData().objectKey(objectKey).build();
    }

    @Override
    public List<PhotoData> findAllPhotos() {
        List<URL> allUrls = photoStorage.allImagesUrls();
        return allUrls.stream().map(url -> photoData().accessUrl(url).build()).collect(toList());
    }

    @Override
    public PhotoData findPhotoByKey(String objectKey) {
        URL accessUrl = photoStorage.imageUrl(objectKey);
        List<String> labels = imageRecognition.labels(objectKey);
        return photoData().objectKey(objectKey).accessUrl(accessUrl).labels(labels).build();
    }
}
