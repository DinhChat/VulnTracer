package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class NucleiFindingCWE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "nuclei_finding_id")
    private NucleiFinding finding;

    @ManyToOne
    @JoinColumn(name = "cwe_id")
    private CWE cwe;
}
