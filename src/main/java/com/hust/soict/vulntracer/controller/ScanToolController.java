package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.model.ScanTool;
import com.hust.soict.vulntracer.request.CreateScanToolRequest;
import com.hust.soict.vulntracer.service.ScanToolService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/tool")
public class ScanToolController {
    private final ScanToolService scanToolService;

    public ScanToolController(ScanToolService scanToolService) {
        this.scanToolService = scanToolService;
    }

    @PostMapping("/create")
    public ResponseEntity<ScanTool> createScanTool(@RequestBody CreateScanToolRequest req) throws ResponseStatusException {
        return new ResponseEntity<>(scanToolService.createScanTool(req),HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ScanTool>> getAllScanTool() throws ResponseStatusException {
        return new ResponseEntity<>(scanToolService.getAllScanTool(), HttpStatus.OK);
    }
}
