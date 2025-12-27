package com.hust.soict.vulntracer.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ScanRequest {
    private String scanId;
    private String targetUrl;
    private List<String> scanTools;
    private String callbackUrl;
    private Map<String, Object> scanParameters;
}
