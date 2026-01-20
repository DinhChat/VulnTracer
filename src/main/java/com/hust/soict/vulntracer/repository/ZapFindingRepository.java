package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.model.ZapFinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZapFindingRepository extends JpaRepository<ZapFinding, Long> {
    List<ZapFinding> findByScan(Scan scan);
}
