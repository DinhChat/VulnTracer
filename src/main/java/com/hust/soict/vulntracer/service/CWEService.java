package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.CWE;
import com.hust.soict.vulntracer.response.CWEItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface CWEService {
    CWE getCWEById(String id) throws ResponseStatusException;
    CWE findCWEByCweNum(Integer cweNum) throws ResponseStatusException;
    Page<CWEItemResponse> getAllCWE(Pageable pageable);
}
