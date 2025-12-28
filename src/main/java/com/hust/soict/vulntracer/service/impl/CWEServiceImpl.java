package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.CWE;
import com.hust.soict.vulntracer.repository.CWERepository;
import com.hust.soict.vulntracer.service.CWEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CWEServiceImpl implements CWEService {
    private final CWERepository CWERepository;

    @Autowired
    public CWEServiceImpl(CWERepository CWERepository) {
        this.CWERepository = CWERepository;
    }

    @Override
    public CWE getCWEById(String id) throws ResponseStatusException {
        CWE CWE = CWERepository.findByCweId(id);
        if (CWE == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Vulnerable not found");
        }
        return CWE;
    }

    @Override
    public List<CWE> getAllCWE() throws ResponseStatusException {
        List<CWE> CWEList = CWERepository.findAll();
        if (CWEList.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Vulnerable not found");
        }
        return CWEList;
    }

    @Override
    public CWE findCWEByCweNum(Integer cweNum) throws ResponseStatusException {
        CWE CWE = CWERepository.findByCweNum(cweNum);
        if (CWE == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Vulnerable not found");
        }
        return CWE;
    }
}
