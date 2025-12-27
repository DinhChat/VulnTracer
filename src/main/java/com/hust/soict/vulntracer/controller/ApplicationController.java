package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.response.ApplicationResponse;
import com.hust.soict.vulntracer.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/target")
public class ApplicationController {
    private ApplicationService applicationService;

    @GetMapping
    public List<ApplicationResponse> getAllApplication() {
        return applicationService.getAllApplication();
    }
}
