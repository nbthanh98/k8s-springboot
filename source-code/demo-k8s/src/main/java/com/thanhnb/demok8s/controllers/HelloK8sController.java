package com.thanhnb.demok8s.controllers;

import com.thanhnb.demok8s.configs.ApplicationConfigs;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloK8sController {

    private final ApplicationConfigs applicationConfigs;

    public HelloK8sController(ApplicationConfigs applicationConfigs) {
        this.applicationConfigs = applicationConfigs;
    }

    @RequestMapping(value = "/hello-k8s", method = RequestMethod.GET)
    public String helloK8s() {
        return "/hello-k8s";
    }


    @RequestMapping(value = "/v2/hello-k8s", method = RequestMethod.GET)
    public String helloK8sV2() {
        return "/v2/hello-k8s";
    }


    @RequestMapping(value = "/v2/get-all-env", method = RequestMethod.GET)
    public String getAllEnv() {
        return applicationConfigs.toString();
    }
}
