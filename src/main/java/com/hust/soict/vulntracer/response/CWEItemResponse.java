package com.hust.soict.vulntracer.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CWEItemResponse {
    private String cweId;
    private Integer cweNum;
    private String cweName;
    @Column(columnDefinition = "TEXT")
    private String shortDescription;
    @Column(columnDefinition = "TEXT")
    private String related;
    private String sourceFile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
