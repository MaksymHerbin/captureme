package com.herbinm.edx.captureme.gateway.controller;

import com.herbinm.edx.captureme.gateway.facade.PhotosFacade;
import com.herbinm.edx.captureme.gateway.facade.data.PhotoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final PhotosFacade photosFacade;

    @Inject
    public HomeController(PhotosFacade photosFacade) {
        this.photosFacade = photosFacade;
    }

    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) String justSavedPhotoKey) {
        LOGGER.trace("Loading all photos");
        if (justSavedPhotoKey != null) {
            LOGGER.trace("A photo with key {} was just saved, obtaining public url and labels", justSavedPhotoKey);
            model.addAttribute("recentUploaded", photosFacade.findPhotoByKey(justSavedPhotoKey));
        }
        model.addAttribute("photos", photosFacade.findAllPhotos());
        return "main";
    }

    @PostMapping("/")
    public ModelAndView uploadPhoto(@RequestParam("photo") MultipartFile multipartFile, ModelMap model) {
        LOGGER.trace("Uploading photo {}, size {}", multipartFile.getOriginalFilename(), multipartFile.getSize());
        PhotoData uploadPhoto = photosFacade.uploadPhoto(multipartFile);
        if (uploadPhoto != null) {
            model.put("justSavedPhotoKey", uploadPhoto.getObjectKey());
        }
        return new ModelAndView("redirect:/", model);
    }

}
