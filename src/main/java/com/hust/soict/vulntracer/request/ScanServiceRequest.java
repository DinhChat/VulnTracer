package com.hust.soict.vulntracer.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ScanServiceRequest {
    private Long scan_id;
    private String target_url;
    private List<String> scan_tools;
    private String callback_url;
    private Map<String, Object> scan_parameters;
}
