package com.hust.soict.vulntracer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanTool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long toolId;
    private String toolName;
    private String dockerImage;
    private String toolDescription;
    private String defaultArgs;
}
