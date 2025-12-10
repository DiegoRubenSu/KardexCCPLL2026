package com.kardex.kardex.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Kardex CCPLL - ONLINE";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}