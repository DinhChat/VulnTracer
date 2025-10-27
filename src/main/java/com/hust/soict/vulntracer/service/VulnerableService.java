package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.Vulnerable;

import java.util.List;

public interface VulnerableService {
    Vulnerable findVulnerableById(String id) throws Exception;
    List<Vulnerable> getAllVulnerable() throws Exception;
}
