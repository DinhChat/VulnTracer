package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.request.CallbackRequest;
import com.hust.soict.vulntracer.request.CreateScanRequest;
import com.hust.soict.vulntracer.response.ScanResponse;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface ScanService {
    ScanResponse  createScan(CreateScanRequest request, String username) throws ResponseStatusException;
    List<ScanResponse> getScansByApplication(Long applicationId) throws ResponseStatusException;
    List<ScanResponse> getAllMyScan(String username) throws ResponseStatusException;
    ScanResponse addScan(Long applicationId, CreateScanRequest request, String username) throws ResponseStatusException;
    void handleCallback(CallbackRequest req) throws ResponseStatusException;
}
