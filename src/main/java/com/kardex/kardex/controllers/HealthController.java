package com.kardex.kardex.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "Kardex CCPLL está funcionando correctamente!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/status")
    public String status() {
        return "{\"status\":\"UP\",\"service\":\"kardex\"}";
    }
}