package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.request.FromUserScanRequest;

public interface ScanService {
    Scan createScan(FromUserScanRequest request) throws Exception;
}
