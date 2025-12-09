package com.hust.soict.vulntracer.request;

import com.hust.soict.vulntracer.model.SCAN_STATUS;
import lombok.Data;

@Data
public class ScanResponse {
    private Long scanId;
    private SCAN_STATUS status;
    private String message;
    private String error;
}
