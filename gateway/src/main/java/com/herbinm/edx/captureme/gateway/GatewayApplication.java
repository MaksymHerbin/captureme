package com.herbinm.edx.captureme.gateway;

import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

@SpringBootApplication
public class GatewayApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("main");
    }

    @Bean
    public Filter tracingFilter() {
        return new AWSXRayServletFilter("CaptureMe");
    }

    public static class SessionAttribute {
        public static final String CURRENT_USER = "current_user";
    }

}
