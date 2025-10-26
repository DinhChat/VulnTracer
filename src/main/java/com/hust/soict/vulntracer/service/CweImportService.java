package com.hust.soict.vulntracer.service;

import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CweImportService {
    @Transactional
    Map<String, Object> importCweXml(MultipartFile file) throws Exception;
}
