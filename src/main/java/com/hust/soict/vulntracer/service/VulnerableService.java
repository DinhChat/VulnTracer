package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.CommonWeaknessEnumeration;

import java.util.List;

public interface VulnerableService {
    CommonWeaknessEnumeration findVulnerableById(String id) throws Exception;
    List<CommonWeaknessEnumeration> getAllVulnerable() throws Exception;
}
