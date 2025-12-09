package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.Scan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanRepository extends JpaRepository<Scan, Long> {
}
