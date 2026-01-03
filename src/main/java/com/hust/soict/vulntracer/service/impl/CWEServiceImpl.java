package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.CWE;
import com.hust.soict.vulntracer.repository.CWERepository;
import com.hust.soict.vulntracer.response.CWEItemResponse;
import com.hust.soict.vulntracer.service.CWEService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public CWE findCWEByCweNum(Integer cweNum) throws ResponseStatusException {
        CWE CWE = CWERepository.findByCweNum(cweNum);
        if (CWE == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Vulnerable not found");
        }
        return CWE;
    }

    @Override
    public Page<CWEItemResponse> getAllCWE(Pageable pageable) {
        Page<CWE> cwePage = CWERepository.findAll(pageable);

        if (cwePage.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Vulnerable not found"
            );
        }

        return cwePage.map(this::toCWEItemResponse);
    }

    private CWEItemResponse toCWEItemResponse(CWE cwe) {
        CWEItemResponse cweItemResponse = new CWEItemResponse();
        cweItemResponse.setCweId(cwe.getCweId());
        cweItemResponse.setCweNum(cwe.getCweNum());
        cweItemResponse.setCweName(cwe.getCweName());
        cweItemResponse.setShortDescription(cwe.getShortDescription());
        cweItemResponse.setRelated(cwe.getRelated());
        cweItemResponse.setSourceFile(cwe.getSourceFile());
        cweItemResponse.setCreatedAt(cwe.getCreatedAt());
        cweItemResponse.setUpdatedAt(cwe.getUpdatedAt());
        return cweItemResponse;
    }
}
