package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.service.CweImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminImportController {
    private static final Logger logger = LoggerFactory.getLogger(AdminImportController.class);
    private final CweImportService importService;

    public AdminImportController(CweImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/upload-cwe")
    public ResponseEntity<?> uploadCwe(@RequestParam("file") MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase().endsWith(".xml")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only XML files are allowed"));
        }

        try {
            Map<String, Object> result = importService.importCweXml(file);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Failed to import CWE file: {}", name, e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Import failed",
                    "message", e.getMessage()
            ));
        }
    }
}
