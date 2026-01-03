package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.ScanTool;
import com.hust.soict.vulntracer.repository.ScanToolRepository;
import com.hust.soict.vulntracer.request.CreateScanToolRequest;
import com.hust.soict.vulntracer.service.ScanToolService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ScanToolServiceImpl implements ScanToolService {
    private final ScanToolRepository scanToolRepository;

    public ScanToolServiceImpl(ScanToolRepository scanToolRepository) {
        this.scanToolRepository = scanToolRepository;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ScanTool createScanTool(CreateScanToolRequest req) throws ResponseStatusException {
        ScanTool scanTool = new ScanTool();
        scanTool.setToolName(req.getToolName());
        scanTool.setToolDescription(req.getToolDescription());
        return scanToolRepository.save(scanTool);
    }

    @Override
    public List<ScanTool> getAllScanTool() throws ResponseStatusException {
        return scanToolRepository.findAll();
    }
}
