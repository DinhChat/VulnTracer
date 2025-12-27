package com.hust.soict.vulntracer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationResponse {
    private Long applicationId;
    private String applicationName;
    private String applicationUrl;
    private String applicationType;
    private String applicationStatus;
    private LocalDateTime applicationCreatedAt;
    private LocalDateTime lastScannedAt;
}
