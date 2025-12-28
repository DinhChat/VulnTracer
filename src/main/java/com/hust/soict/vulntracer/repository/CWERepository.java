package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.CWE;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CWERepository extends JpaRepository<CWE, String> {
    CWE findByCweId(String cweId);
    CWE findByCweNum(Integer cweNum);
}
