package com.hust.soict.vulntracer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanServiceRawResponse {
    @JsonProperty("scan_id")
    private String scanId;
    private String status;
    private String message;
    @JsonProperty("queued_at")
    private String queuedAt;
}

