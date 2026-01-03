package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.NucleiFinding;
import com.hust.soict.vulntracer.model.Scan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NucleiFindingRepository extends JpaRepository<NucleiFinding, Long> {
    List<NucleiFinding> findByScan(Scan scan);
}
