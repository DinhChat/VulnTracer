package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private LocalDateTime applicationCreatedAt;
    private LocalDateTime applicationUpdatedAt;
}
