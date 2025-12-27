package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.request.CreateScanRequest;

public interface ScanService {
    Scan createScan(CreateScanRequest request) throws Exception;
}
