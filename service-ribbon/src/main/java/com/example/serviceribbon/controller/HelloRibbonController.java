package com.example.serviceribbon.controller;

import com.example.serviceribbon.service.HelloRibbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloRibbonController {

    @Autowired
    HelloRibbonService helloRibbonService;
    @RequestMapping(value = "/hi")
    public String hi(@RequestParam String name){
        return helloRibbonService.hiService(name);
    }

}
