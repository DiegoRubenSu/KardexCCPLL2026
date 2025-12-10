package com.kardexccpll.kardex.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "Kardex CCPLL - Sistema de Inventarios";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}