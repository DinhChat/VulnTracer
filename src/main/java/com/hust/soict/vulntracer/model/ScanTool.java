package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
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
    @Column(unique = true, nullable = false)
    private String toolName;
    private String toolDescription;
}
