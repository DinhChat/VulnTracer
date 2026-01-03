package com.hust.soict.vulntracer.response;

import com.hust.soict.vulntracer.model.SCAN_STATUS;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScanInfoResponse {
    private Long scanId;
    private String applicationName;
    private LocalDateTime startTime;
    private LocalDateTime completedAt;
    private SCAN_STATUS status;
}
