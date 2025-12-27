package com.hust.soict.vulntracer.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateApplicationRequest {
    private String applicationUrl;
    private String applicationName;
    private String applicationType;
    private String applicationStatus;
    private String applicationDescription;
    private LocalDateTime applicationCreatedAt;
    private LocalDateTime applicationUpdatedAt;
    private LocalDateTime lastScannedAt;
}
