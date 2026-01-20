package com.hust.soict.vulntracer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZapEvidenceResponse {
    private Long zapEvidenceId;

    private String uri;
    private String method;
    private String param;
    private String evidence;
}
