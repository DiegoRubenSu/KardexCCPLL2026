package com.kardex.kardex.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiDocsController {

    @GetMapping("/api-docs")
    public String mostrarApiDocs() {
        return "api-docs";
    }
}