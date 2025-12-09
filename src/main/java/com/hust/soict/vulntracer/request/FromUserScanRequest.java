package com.hust.soict.vulntracer.request;

import lombok.Data;

import java.util.List;

@Data
public class FromUserScanRequest {
    private String targetName;
    private String targetUrl;
    private String targetType;
    private String targetDescription;
    private List<String> scanTools;
}
