package com.hust.soict.vulntracer.response;

import com.hust.soict.vulntracer.model.SCAN_STATUS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanResponse {
    private String scanId;
    private SCAN_STATUS status;
    private String message;
    private LocalDateTime startedAt;
    private LocalDateTime queuedAt;
}
