package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.ZapFinding;
import com.hust.soict.vulntracer.repository.ZapEvidenceRepository;
import com.hust.soict.vulntracer.repository.ZapFindingRepository;
import com.hust.soict.vulntracer.response.ZapEvidenceResponse;
import com.hust.soict.vulntracer.response.ZapFindingDetailResponse;
import com.hust.soict.vulntracer.service.ZapFindingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ZapFindingServiceImpl implements ZapFindingService {
    private final ZapFindingRepository zapFindingRepository;
    private final ZapEvidenceRepository zapEvidenceRepository;

    public ZapFindingServiceImpl(
            ZapFindingRepository zapFindingRepository,
            ZapEvidenceRepository zapEvidenceRepository
    ) {
        this.zapFindingRepository = zapFindingRepository;
        this.zapEvidenceRepository = zapEvidenceRepository;
    }

    @Override
    public ZapFindingDetailResponse getDetailZapFinding(Long zapFindingId) throws ResponseStatusException {
        ZapFinding zapFinding =  zapFindingRepository.findById(zapFindingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Zap Finding Not Found"));


        ZapFindingDetailResponse res = new ZapFindingDetailResponse();
        res.setZapFindingId(zapFindingId);
        res.setName(zapFinding.getName());
        res.setDescription(zapFinding.getDescription());
        res.setConfidence(zapFinding.getConfidence());
        res.setSeverity(zapFinding.getSeverity());
        res.setSolution(zapFinding.getSolution());
        res.setWascId(zapFinding.getWascId());
        res.setPluginId(zapFinding.getPluginId());
        res.setCweId(zapFinding.getCweId());

        List<ZapEvidenceResponse> evidences = zapEvidenceRepository.findByZapFinding(zapFinding)
                .stream()
                .map(ev -> {
                    ZapEvidenceResponse e = new ZapEvidenceResponse();
                    e.setZapEvidenceId(ev.getZapEvidenceId());
                    e.setUri(ev.getUri());
                    e.setParam(ev.getParam());
                    e.setMethod(ev.getMethod());
                    e.setEvidence(ev.getEvidence());
                    return e;
                })
                .toList();
        res.setEvidences(evidences);

        return res;

    }
}
