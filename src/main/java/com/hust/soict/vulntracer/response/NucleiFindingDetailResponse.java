package com.hust.soict.vulntracer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NucleiFindingDetailResponse {
    private Long id;
    private String templateId;
    private String name;
    private String severity;
    private String description;
    private String matchedAt;

    private List<NucleiEvidenceResponse> evidences;
    private List<CWEResponse> cwe;
}
