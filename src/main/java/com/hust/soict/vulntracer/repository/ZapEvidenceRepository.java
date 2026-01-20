package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.ZapEvidence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZapEvidenceRepository extends JpaRepository<ZapEvidence, Long> {
}
