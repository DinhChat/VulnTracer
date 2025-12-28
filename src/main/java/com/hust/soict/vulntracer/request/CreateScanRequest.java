package com.hust.soict.vulntracer.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateScanRequest {
    private List<ScanToolRequest> scanTools;
    private String applicationUrl;
    private String applicationName;
    private String applicationType;
    private String applicationDescription;
}
