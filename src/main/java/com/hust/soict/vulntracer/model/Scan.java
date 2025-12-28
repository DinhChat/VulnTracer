package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scanId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SCAN_STATUS status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "scan_tools",
            joinColumns = @JoinColumn(name = "scan_id")
    )
    @Column(name = "tool_name")
    private List<String> scanTools = new ArrayList<>();
}
