package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.request.CreateScanRequest;
import com.hust.soict.vulntracer.response.ScanResponse;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface ScanService {
    Scan createScan(CreateScanRequest request) throws Exception;
    List<ScanResponse> getScansByApplication(Long applicationId) throws ResponseStatusException;
    List<ScanResponse> getAllMyScan(String username) throws ResponseStatusException;
    ScanResponse addScan(Long applicationId, String username) throws ResponseStatusException;
}
