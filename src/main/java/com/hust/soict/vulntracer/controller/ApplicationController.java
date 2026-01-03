package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.response.ApplicationResponse;
import com.hust.soict.vulntracer.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/application")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>>  getAllApplication(
            Authentication authentication
    ) throws ResponseStatusException {
        String username = authentication.getName();
        List<ApplicationResponse> responses = applicationService.getAllApplication(username);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
