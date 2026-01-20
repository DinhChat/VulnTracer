package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZapEvidence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long zapEvidenceId;

    @ManyToOne
    @JoinColumn(name = "zap_finding_id")
    private ZapFinding zapFinding;

    private String uri;
    private String method;
    private String param;
    @Column(columnDefinition = "LONGTEXT")
    private String evidence;
}
