package com.hust.soict.vulntracer.response;

import lombok.Data;

@Data
public class ZapFindingResponse {
    private Long zapFindingId;
    private String pluginId;
    private String name;
    private String confidence;
    private String severity;
    private String description;
    private String solution;
}
