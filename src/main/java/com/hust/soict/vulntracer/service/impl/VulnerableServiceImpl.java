package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.Vulnerable;
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
    public Vulnerable findVulnerableById(String id) throws Exception {
        Vulnerable vulnerable = vulnerableRepository.findByCweId(id);
        if (vulnerable == null) {
            throw new Exception("Vulnerable not found");
        }
        return vulnerable;
    }

    @Override
    public List<Vulnerable> getAllVulnerable() throws Exception {
        List<Vulnerable> vulnerableList = vulnerableRepository.findAll();
        if (vulnerableList.isEmpty()) {
            throw new Exception("Vulnerable not found");
        }
        return vulnerableList;
    }
}
