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
public class Scan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sId;
    @ManyToOne
    @JoinColumn(name = "u_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "target_id")
    private TargetApplication targetApplication;
    @ManyToOne
    @JoinColumn(name = "tool_id")
    private ScanTool tool;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SCAN_STATUS status;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
