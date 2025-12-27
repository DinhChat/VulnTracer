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
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String applicationUrl;
    private String applicationName;
    private String applicationType;
    private String applicationStatus;
    private String applicationDescription;
    private LocalDateTime applicationCreatedAt;
    private LocalDateTime applicationUpdatedAt;
    private LocalDateTime lastScannedAt;
}
