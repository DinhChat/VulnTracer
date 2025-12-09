package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.CommonWeaknessEnumeration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VulnerableRepository extends JpaRepository<CommonWeaknessEnumeration, String> {
    CommonWeaknessEnumeration findByCweId(String cweId);
    CommonWeaknessEnumeration findByCweNum(Integer cweNum);
}
