package com.thanhnb.demok8s.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloK8sController {

    @RequestMapping(value = "/hello-k8s", method = RequestMethod.GET)
    public String helloK8s() {
        return "/hello-k8s";
    }
}
