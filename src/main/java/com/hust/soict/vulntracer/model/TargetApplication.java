package com.hust.soict.vulntracer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TargetApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long target_id;
    private String application_url;
    private String application_name;
    private String application_type;
    private String application_status;
    private String application_description;
    private String application_created_at;
    private String application_updated_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid")
    private User owner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "target_vulnerable",
            joinColumns = @JoinColumn(name = "target_id"),
            inverseJoinColumns = @JoinColumn(name = "cwe_id")
    )
    private Set<Vulnerable> vulnerable = new HashSet<>();
}
