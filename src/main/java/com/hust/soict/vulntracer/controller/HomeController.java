package com.hust.soict.vulntracer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "dashboard";
    }

    @GetMapping("/home")
    public String getHome() {
        return "dashboard";
    }

    @GetMapping("auth/login")
    public String login() {
        return "login";
    }

    @GetMapping("auth/register")
    public String register() {
        return "register";
    }
}
