package com.hust.soict.vulntracer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long target_id;
    private String application_url;
    private String application_name;
    private String application_type;
    private String application_status;
    private String application_description;
    private String application_created_at;
    private String application_updated_at;
}
