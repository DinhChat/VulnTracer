package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.NucleiFinding;
import com.hust.soict.vulntracer.model.NucleiFindingCWE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NucleiFindingCweRepository extends JpaRepository<NucleiFindingCWE, Long> {
    List<NucleiFindingCWE> findByFinding(NucleiFinding finding);
}
