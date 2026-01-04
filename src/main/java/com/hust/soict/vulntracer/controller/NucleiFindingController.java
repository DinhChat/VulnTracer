package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.response.NucleiFindingDetailResponse;
import com.hust.soict.vulntracer.service.NucleiFindingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/vulnerability")
public class NucleiFindingController {
    private final NucleiFindingService nucleiFindingService;

    public NucleiFindingController(
            NucleiFindingService nucleiFindingService
    ) {
        this.nucleiFindingService = nucleiFindingService;
    }

    @GetMapping("/{nucleiFindingId}")
    public ResponseEntity<NucleiFindingDetailResponse> getNucleiFindingDetail(@PathVariable Long nucleiFindingId) throws ResponseStatusException {
        return new ResponseEntity<>(nucleiFindingService.getDetailNucleiFinding(nucleiFindingId), HttpStatus.OK);
    }
}
