package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.ZapFindingCWE;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZapFindingCweRepository extends JpaRepository<ZapFindingCWE, Long> {
}
