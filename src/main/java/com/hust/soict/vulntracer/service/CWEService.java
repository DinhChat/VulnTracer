package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.CWE;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface CWEService {
    CWE getCWEById(String id) throws ResponseStatusException;
    List<CWE> getAllCWE() throws ResponseStatusException;
    CWE findCWEByCweNum(Integer cweNum) throws ResponseStatusException;
}
