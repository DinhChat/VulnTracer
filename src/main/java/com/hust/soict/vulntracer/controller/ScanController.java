package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.service.ScanService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scan")
@CrossOrigin
public class ScanController {

    public ScanController(ScanService scanService) {
    }

}
