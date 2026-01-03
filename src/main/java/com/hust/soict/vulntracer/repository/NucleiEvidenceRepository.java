package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.NucleiEvidence;
import com.hust.soict.vulntracer.model.NucleiFinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NucleiEvidenceRepository extends JpaRepository<NucleiEvidence, Long> {
    List<NucleiEvidence> findByNucleiFinding(NucleiFinding finding);
}
