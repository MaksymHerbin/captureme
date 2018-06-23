package com.herbinm.edx.captureme.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.herbinm.edx.captureme.gateway.security.SessionAttributes.CURRENT_USER_SESSION;

@EnableWebMvc
@Configuration
public class SecurityConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/myphotos");
    }

    private class LoginInterceptor extends HandlerInterceptorAdapter {

        private final Logger LOGGER = LoggerFactory.getLogger(LoginInterceptor.class);

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            LOGGER.info("Checking if user is already logged into application");
            HttpSession session = request.getSession();
            if (session.getAttribute(CURRENT_USER_SESSION) == null) {
                response.sendRedirect("/auth/login");
            }
            return session.getAttribute(CURRENT_USER_SESSION) != null;
        }
    }
}