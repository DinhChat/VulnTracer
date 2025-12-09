package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.CommonWeaknessEnumeration;
import com.hust.soict.vulntracer.repository.VulnerableRepository;
import com.hust.soict.vulntracer.service.VulnerableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VulnerableServiceImpl implements VulnerableService {
    private final VulnerableRepository vulnerableRepository;

    @Autowired
    public VulnerableServiceImpl(VulnerableRepository vulnerableRepository) {
        this.vulnerableRepository = vulnerableRepository;
    }

    @Override
    public CommonWeaknessEnumeration findVulnerableById(String id) throws Exception {
        CommonWeaknessEnumeration commonWeaknessEnumeration = vulnerableRepository.findByCweId(id);
        if (commonWeaknessEnumeration == null) {
            throw new Exception("Vulnerable not found");
        }
        return commonWeaknessEnumeration;
    }

    @Override
    public List<CommonWeaknessEnumeration> getAllVulnerable() throws Exception {
        List<CommonWeaknessEnumeration> commonWeaknessEnumerationList = vulnerableRepository.findAll();
        if (commonWeaknessEnumerationList.isEmpty()) {
            throw new Exception("Vulnerable not found");
        }
        return commonWeaknessEnumerationList;
    }
}
