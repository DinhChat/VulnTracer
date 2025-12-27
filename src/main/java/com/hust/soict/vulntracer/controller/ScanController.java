package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.response.ScanResponse;
import com.hust.soict.vulntracer.service.ScanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scan")
@CrossOrigin
public class ScanController {
    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @GetMapping("/target/{applicationId}")
    public List<ScanResponse> getScansByTarget(@PathVariable Long applicationId) {
        return scanService.getScansByApplication(applicationId);
    }
}
