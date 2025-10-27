package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.model.Vulnerable;
import com.hust.soict.vulntracer.service.VulnerableService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/vulnerable")
@CrossOrigin
@AllArgsConstructor
@NoArgsConstructor
public class VulnerableController {
    private VulnerableService vulnerableService;

    @GetMapping("/all")
    public List<Vulnerable> getAllVulnerable() throws Exception {
        return vulnerableService.getAllVulnerable();
    }

    @GetMapping("/{id}")
    public Vulnerable findVulnerableById(@PathVariable String id) throws Exception {
        return vulnerableService.findVulnerableById(id);
    }
}
