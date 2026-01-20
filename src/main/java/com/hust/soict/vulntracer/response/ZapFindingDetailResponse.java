package com.hust.soict.vulntracer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZapFindingDetailResponse {
    private Long zapFindingId;
    private String pluginId;
    private String name;
    private String confidence;
    private String severity;
    private String description;
    private String solution;
    private String cweId;
    private String wascId;
    private List<ZapEvidenceResponse> evidences;
}
