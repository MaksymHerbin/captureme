package com.herbinm.edx.captureme.gateway.controller;

import com.herbinm.edx.captureme.gateway.domain.User;
import com.herbinm.edx.captureme.gateway.facade.PhotosFacade;
import com.herbinm.edx.captureme.gateway.facade.data.PhotoData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import static com.herbinm.edx.captureme.gateway.security.SessionAttributes.CURRENT_USER_SESSION;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private final PhotosFacade photosFacade;

    @Inject
    public HomeController(PhotosFacade photosFacade) {
        this.photosFacade = photosFacade;
    }

    @GetMapping("/")
    public String home() {
        return "main";
    }

    @GetMapping("/myphotos")
    public String myphotos(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute(CURRENT_USER_SESSION);
        LOGGER.trace("Loading all photos for user {}", currentUser.getNickname());
        model.addAttribute("photos", photosFacade.findAllPhotos(currentUser));
        return "myphotos";

    }

    @PostMapping("/myphotos")
    public String uploadPhoto(@RequestParam("photo") MultipartFile multipartFile, HttpSession session) {
        User currentUser = (User) session.getAttribute(CURRENT_USER_SESSION);
        LOGGER.trace("Uploading photo {}, size {} for user {}", multipartFile.getOriginalFilename(), multipartFile.getSize(), currentUser.getNickname());
        PhotoData uploadPhoto = photosFacade.uploadPhoto(multipartFile, currentUser);
        return "redirect:/myphotos";

    }

}
