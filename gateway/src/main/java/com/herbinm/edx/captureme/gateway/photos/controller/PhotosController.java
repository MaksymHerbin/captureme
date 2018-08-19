package com.herbinm.edx.captureme.gateway.photos.controller;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.herbinm.edx.captureme.gateway.domain.User;
import com.herbinm.edx.captureme.gateway.photos.facade.PhotosFacade;
import com.herbinm.edx.captureme.gateway.photos.facade.data.PhotoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;

import static com.herbinm.edx.captureme.gateway.GatewayApplication.SessionAttribute.CURRENT_USER;

@Controller
@RequestMapping("/myphotos")
@XRayEnabled
public class PhotosController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhotosController.class);
    private final PhotosFacade photosFacade;

    @Inject
    public PhotosController(PhotosFacade photosFacade) {
        this.photosFacade = photosFacade;
    }

    @GetMapping
    public String myphotos(Model model, @SessionAttribute(CURRENT_USER) User currentUser) {
        LOGGER.trace("Loading all photos for user {}", currentUser.getNickname());
        model.addAttribute("photos", photosFacade.findAllPhotos(currentUser));
        return "myphotos";

    }

    @GetMapping("/delete/{photo_id}")
    public String delete(@PathVariable("photo_id") String photoId, @SessionAttribute(CURRENT_USER) User currentUser) {
        LOGGER.trace("Deleting photo with id {} for user {}", photoId, currentUser);
        photosFacade.deletePhoto(photoId, currentUser);
        return "redirect:/myphotos";
    }

    @PostMapping
    public String uploadPhoto(@RequestParam("photo") MultipartFile multipartFile, @SessionAttribute(CURRENT_USER) User currentUser) {
        LOGGER.trace("Uploading photo {}, size {} for user {}", multipartFile.getOriginalFilename(), multipartFile.getSize(), currentUser.getNickname());
        photosFacade.uploadPhoto(multipartFile, currentUser);
        return "redirect:/myphotos";

    }

}
