package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long targetId;
    private String applicationUrl;
    private String applicationName;
    private String applicationType;
    private String applicationStatus;
    private String applicationDescription;
    private String applicationCreatedAt;
    private String applicationUpdatedAt;
}
