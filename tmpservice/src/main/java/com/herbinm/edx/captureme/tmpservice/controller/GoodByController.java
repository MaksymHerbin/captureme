package com.herbinm.edx.captureme.tmpservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoodByController {

    @RequestMapping(value = "/by")
    public String goodByWorld() {
        return "See ya";
    }


}
