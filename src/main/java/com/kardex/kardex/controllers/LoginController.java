package com.kardex.kardex.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/loginCCPLL")
    public String mostrarLogin() {
        return "loginCCPLL";
    }

}