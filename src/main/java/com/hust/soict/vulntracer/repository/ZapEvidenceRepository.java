package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.ZapEvidence;
import com.hust.soict.vulntracer.model.ZapFinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZapEvidenceRepository extends JpaRepository<ZapEvidence, Long> {
    List<ZapEvidence> findByZapFinding(ZapFinding finding);
}
