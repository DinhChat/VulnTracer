package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.Scan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScanRepository extends JpaRepository<Scan, Long> {
    List<Scan> findByApplication_ApplicationId(Long targetId);
}
