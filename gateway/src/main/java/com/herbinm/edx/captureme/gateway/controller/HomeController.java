package com.herbinm.edx.captureme.gateway.controller;

import com.herbinm.edx.captureme.gateway.service.storage.PhotoStorage;
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
import java.net.URL;

import static java.util.stream.Collectors.toList;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final PhotoStorage photoStorage;

    @Inject
    public HomeController(PhotoStorage photoStorage) {
        this.photoStorage = photoStorage;
    }

    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) String justSavedImageKey) {
        LOGGER.trace("Loading all photos");
        if (justSavedImageKey != null) {
            LOGGER.trace("A photo with key {} was just saved, obtaining public url and labels", justSavedImageKey);
            model.addAttribute("recentUpload", photoStorage.imageUrl(justSavedImageKey));
            model.addAttribute("all_labels", photoStorage.labels(justSavedImageKey));
        }
        model.addAttribute("photos", photoStorage.allImagesUrls().stream().map(URL::toString).collect(toList()));
        return "main";
    }

    @PostMapping("/")
    public ModelAndView uploadPhoto(@RequestParam("photo") MultipartFile multipartFile, ModelMap model) {
        LOGGER.trace("Uploading photo {}, size {}", multipartFile.getOriginalFilename(), multipartFile.getSize());
        String savedImageKey = photoStorage.uploadImage(multipartFile);
        if (savedImageKey != null) {
            model.put("justSavedImageKey", savedImageKey);
        }
        return new ModelAndView("redirect:/", model);
    }

}
