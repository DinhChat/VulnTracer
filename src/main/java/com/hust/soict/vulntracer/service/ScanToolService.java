package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.ScanTool;
import com.hust.soict.vulntracer.request.CreateScanToolRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface ScanToolService {
    ScanTool createScanTool(CreateScanToolRequest req) throws ResponseStatusException;
    List<ScanTool> getAllScanTool() throws ResponseStatusException;
}
