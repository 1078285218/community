package com.community.community.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class HelloController {
    @GetMapping("/")
    public String index(){
        return "index";
    }
}
