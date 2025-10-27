package com.hust.soict.vulntracer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vulnerable {
    @Id
    private String cweId;
    private Integer cweNum;
    private String cweName;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @Column(columnDefinition = "LONGTEXT")
    private String extendedDescription;

    @Column(columnDefinition = "TEXT")
    private String likelihood;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String related;

    @Column(columnDefinition = "LONGTEXT")
    private String example;

    private String sourceFile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "vulnerable")
    @JsonIgnore
    private Set<TargetApplication> targets = new HashSet<>();
}