package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class NucleiFinding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nucleiFindingId;

    @ManyToOne
    @JoinColumn(name = "scan_id")
    private Scan scan;

    private String templateId;
    private String name;
    private String severity;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String matchedAt;
}

