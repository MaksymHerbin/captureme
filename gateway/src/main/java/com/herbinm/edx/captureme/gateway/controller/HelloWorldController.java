package com.herbinm.edx.captureme.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @RequestMapping(value = "/hello")
    public String helloWorldWebService() {
        return "Hello World";
    }

    @RequestMapping
    public String goodByWorld() {
        return "See ya";
    }


}
