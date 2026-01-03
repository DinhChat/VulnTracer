package com.hust.soict.vulntracer.response;

import lombok.Data;

@Data
public class NucleiFindingResponse {
    private Long id;
    private String templateId;
    private String name;
    private String severity;
    private String matchedAt;
}
