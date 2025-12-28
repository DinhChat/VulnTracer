package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.model.CWE;
import com.hust.soict.vulntracer.service.CWEService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController("/vulnerable")
@CrossOrigin
@AllArgsConstructor
@NoArgsConstructor
public class CWEController {
    private CWEService CWEService;

    @GetMapping("/all")
    public List<CWE> getAllVulnerable()
            throws ResponseStatusException {
        return CWEService.getAllCWE();
    }

    @GetMapping("/{cweId}")
    public CWE getVulnerableById(
            @PathVariable String cweId
    ) throws ResponseStatusException {
        return CWEService.getCWEById(cweId);
    }

    @GetMapping("/{cweNum}")
    public CWE findVulnerableById(@PathVariable Integer cweNum) throws ResponseStatusException {
        return CWEService.findCWEByCweNum(cweNum);
    }
}
