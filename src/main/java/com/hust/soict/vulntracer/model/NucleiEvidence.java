package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class NucleiEvidence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evidenceId;

    @ManyToOne
    @JoinColumn(name = "nuclei_finding_id", nullable = false)
    private NucleiFinding nucleiFinding;

    private String type;

    @Column(columnDefinition = "LONGTEXT")
    private String command;

    @ElementCollection
    @CollectionTable(
            name = "nuclei_evidence_resources",
            joinColumns = @JoinColumn(name = "evidence_id")
    )
    @Column(name = "resource", columnDefinition = "TEXT")
    private List<String> resources;
}

