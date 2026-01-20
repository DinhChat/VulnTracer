package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.response.ZapFindingDetailResponse;
import com.hust.soict.vulntracer.service.ZapFindingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/zap_finding")
public class ZapFindingController {
    private final ZapFindingService zapFindingService;

    public ZapFindingController(ZapFindingService zapFindingService) {
        this.zapFindingService = zapFindingService;
    }

    @GetMapping("/{zapFindingId}")
    public ResponseEntity<ZapFindingDetailResponse> getDetailZapFinding(@PathVariable Long zapFindingId) throws ResponseStatusException {
        return new ResponseEntity<>(zapFindingService.getDetailZapFinding(zapFindingId), HttpStatus.OK);
    }
}
