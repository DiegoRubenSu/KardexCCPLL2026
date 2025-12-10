package com.kardex.kardex.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/monitoring")
public class MonitoringController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("service", "Sistema de Inventarios CCPLL");
        healthInfo.put("version", "1.0.0");
        return healthInfo;
    }

    @GetMapping("/status")
    public String status() {
        return "Sistema funcionando correctamente - " + LocalDateTime.now();
    }
}