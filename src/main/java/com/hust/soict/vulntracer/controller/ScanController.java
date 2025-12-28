package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.request.CreateScanRequest;
import com.hust.soict.vulntracer.response.ScanResponse;
import com.hust.soict.vulntracer.service.ScanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping
    public ResponseEntity<ScanResponse> createScan(
            @RequestBody CreateScanRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        ScanResponse response = scanService.createScan(request, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<ScanResponse>> getScansByApplication(@PathVariable Long applicationId) {
        List<ScanResponse> responses = scanService.getScansByApplication(applicationId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<List<ScanResponse>> getAllMyScan(
            Authentication authentication
    ) {
        String username = authentication.getName();
        List<ScanResponse> responses = scanService.getAllMyScan(username);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/application/{applicationId}")
    public ResponseEntity<ScanResponse> addScan(
            @PathVariable Long applicationId,
            @RequestBody CreateScanRequest request,
            Authentication authentication
            ) {
        String username = authentication.getName();
        ScanResponse res = scanService.addScan(applicationId, request, username);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}
