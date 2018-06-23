package com.herbinm.edx.captureme.gateway.authentication.controller;

import com.herbinm.edx.captureme.gateway.authentication.service.CognitoAuthentication;
import com.herbinm.edx.captureme.gateway.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import static com.herbinm.edx.captureme.gateway.GatewayApplication.SessionAttribute.CURRENT_USER;

@Controller
@RequestMapping("/auth")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private static final String CSRF_STATE_SESSION = "csrf_state";

    private final CognitoAuthentication cognitoAuthentication;

    @Inject
    public LoginController(CognitoAuthentication cognitoAuthentication) {
        this.cognitoAuthentication = cognitoAuthentication;
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
            session.setAttribute(CURRENT_USER, loggedInUser);
        }
        return "redirect:/myphotos";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute(CURRENT_USER);
        return "redirect:/";
    }


}
