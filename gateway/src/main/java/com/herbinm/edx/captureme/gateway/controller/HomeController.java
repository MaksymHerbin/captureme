package com.herbinm.edx.captureme.gateway.controller;

import com.herbinm.edx.captureme.gateway.service.ImageStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.net.URL;

import static java.util.stream.Collectors.toList;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final ImageStorage imageStorage;

    @Inject
    public HomeController(ImageStorage imageStorage) {
        this.imageStorage = imageStorage;
    }

    @GetMapping("/")
    public String home(Model model) {
        LOGGER.info("Loading all photos");
        model.addAttribute("photos", imageStorage.allImagesUrls().stream().map(URL::toString).collect(toList()));
        return "main";
    }

    @PostMapping("/")
    public String uploadPhoto(@RequestParam("photo") MultipartFile multipartFile) {
        LOGGER.info("Uploading photo {}, size {}", multipartFile.getOriginalFilename(), multipartFile.getSize());
        imageStorage.saveImage(multipartFile);
        return "redirect:/";
    }

}
