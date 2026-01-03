package com.hust.soict.vulntracer.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateScanToolRequest {
    private String toolName;
    private String toolDescription;
}
