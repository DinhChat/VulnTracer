package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.Vulnerable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VulnerableRepository extends JpaRepository<Vulnerable, String> {
    Vulnerable findByCweId(String cweId);
    Vulnerable findByCweNum(Integer cweNum);
}
