package com.hust.soict.vulntracer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanResultResponse {
    private ScanInfoResponse scan;
    private VulnerabilitySummaryResponse summary;
    private List<NucleiFindingResponse> vulnerabilities;
}
