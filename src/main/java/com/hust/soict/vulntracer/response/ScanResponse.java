package com.hust.soict.vulntracer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hust.soict.vulntracer.model.SCAN_STATUS;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScanResponse {
    @JsonProperty("scan_id")
    private Long scanId;
    private SCAN_STATUS status;
    private String message;
    @JsonProperty("queued_at")
    private LocalDateTime queuedAt;
}
