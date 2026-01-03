package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.model.CWE;
import com.hust.soict.vulntracer.response.CWEItemResponse;
import com.hust.soict.vulntracer.service.CWEService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController("/cwe")
@CrossOrigin
@AllArgsConstructor
@NoArgsConstructor
public class CWEController {
    private CWEService CWEService;

    @GetMapping
    public ResponseEntity<Page<CWEItemResponse>> getAllVulnerable(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(CWEService.getAllCWE(pageable));
    }

    @GetMapping("/id/{cweId}")
    public CWE getVulnerableById(
            @PathVariable String cweId
    ) throws ResponseStatusException {
        return CWEService.getCWEById(cweId);
    }

    @GetMapping("/num/{cweNum}")
    public CWE findVulnerableById(@PathVariable Integer cweNum) throws ResponseStatusException {
        return CWEService.findCWEByCweNum(cweNum);
    }
}
