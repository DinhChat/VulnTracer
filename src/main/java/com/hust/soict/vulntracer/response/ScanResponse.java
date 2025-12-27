package com.hust.soict.vulntracer.response;

import com.hust.soict.vulntracer.model.SCAN_STATUS;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScanResponse {
    private Long scanId;
    private SCAN_STATUS status;
    private String message;
    private LocalDateTime queuedAt;
}
