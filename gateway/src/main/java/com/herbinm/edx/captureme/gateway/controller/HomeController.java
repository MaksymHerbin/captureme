package com.herbinm.edx.captureme.gateway.controller;

import com.herbinm.edx.captureme.gateway.domain.User;
import com.herbinm.edx.captureme.gateway.facade.PhotosFacade;
import com.herbinm.edx.captureme.gateway.facade.data.PhotoData;
import com.herbinm.edx.captureme.gateway.service.authentification.CognitoAuthentication;
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
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    private static final String CSRF_STATE_SESSION = "csrf_state";
    private static final String CURRENT_USER_SESSION = "current_user";

    private final PhotosFacade photosFacade;
    private final CognitoAuthentication cognitoAuthentication;

    @Inject
    public HomeController(PhotosFacade photosFacade, CognitoAuthentication cognitoAuthentication) {
        this.photosFacade = photosFacade;
        this.cognitoAuthentication = cognitoAuthentication;
    }

    @GetMapping("/")
    public String home() {
        return "main";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute(CSRF_STATE_SESSION, state);
        LOGGER.info("Redirecting to authentication page for state: {}", state);
        return "redirect:" + cognitoAuthentication.buildLoginUrl(state);
    }

    @GetMapping("/callback")
    public String authCallback(HttpSession session, @RequestParam("code") String code, @RequestParam("state") String state) {
        LOGGER.info("Received authentication callback for state: {}", state);
        if (state.equals(session.getAttribute(CSRF_STATE_SESSION))) {
            LOGGER.info("Authenticating user using code: {}", code);
            User loggedInUser = cognitoAuthentication.authenticate(code);
            session.setAttribute(CURRENT_USER_SESSION, loggedInUser);
        }
        return "main";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(CURRENT_USER_SESSION);
        return "main";
    }
    
    @GetMapping("/myphotos")
    public String myphotos(Model model, HttpSession session, @RequestParam(required = false) String justSavedPhotoKey) {
        User currentUser = (User) session.getAttribute(CURRENT_USER_SESSION);
        if(currentUser != null){
            LOGGER.trace("Loading all photos");
            if (justSavedPhotoKey != null) {
                LOGGER.trace("A photo with key {} was just saved, obtaining public url and labels", justSavedPhotoKey);
                model.addAttribute("recentUploaded", photosFacade.findPhotoByKey(justSavedPhotoKey));
            }
            model.addAttribute("photos", photosFacade.findAllPhotos(currentUser));
            return "myphotos";
        } else{
            return "main";
        }
    }

    @PostMapping("/myphotos")
    public ModelAndView uploadPhoto(@RequestParam("photo") MultipartFile multipartFile, ModelMap model, HttpSession session) {
        User currentUser = (User) session.getAttribute(CURRENT_USER_SESSION);
        if(currentUser != null){
            LOGGER.trace("Uploading photo {}, size {}", multipartFile.getOriginalFilename(), multipartFile.getSize());
            PhotoData uploadPhoto = photosFacade.uploadPhoto(multipartFile, currentUser);
            if (uploadPhoto != null) {
                model.put("justSavedPhotoKey", uploadPhoto.getObjectKey());
            }
            return new ModelAndView("redirect:/myphotos", model);
        } else{
            return new ModelAndView("redirect:/", model);
        }
    }

}
