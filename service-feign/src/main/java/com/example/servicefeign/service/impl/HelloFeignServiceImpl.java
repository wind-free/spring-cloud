package com.example.servicefeign.service.impl;

import com.example.servicefeign.service.HelloFeignService;
import org.springframework.stereotype.Component;

@Component
public class HelloFeignServiceImpl implements HelloFeignService {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry"+name;
    }
}
