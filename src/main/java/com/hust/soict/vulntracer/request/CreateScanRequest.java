package com.hust.soict.vulntracer.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateScanRequest {
    private Long applicationId;
    private String applicationUrl;
    private String applicationName;
    private String applicationType;
    private String applicationDescription;
    private List<String> scanTools;
}
