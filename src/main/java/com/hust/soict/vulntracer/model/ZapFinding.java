package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZapFinding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long zapFindingId;

    @ManyToOne
    @JoinColumn(name = "scan_id")
    private Scan scan;

    private String pluginId;
    private String name;
    private String confidence;
    private String severity;
    private String description;
    private String solution;
}
