package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.NucleiFinding;
import com.hust.soict.vulntracer.repository.NucleiEvidenceRepository;
import com.hust.soict.vulntracer.repository.NucleiFindingCweRepository;
import com.hust.soict.vulntracer.repository.NucleiFindingRepository;
import com.hust.soict.vulntracer.response.CWEResponse;
import com.hust.soict.vulntracer.response.NucleiEvidenceResponse;
import com.hust.soict.vulntracer.response.NucleiFindingDetailResponse;
import com.hust.soict.vulntracer.service.NucleiFindingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NucleiFindingServiceImpl implements NucleiFindingService {
    private final NucleiFindingRepository nucleiFindingRepository;
    private final NucleiEvidenceRepository nucleiEvidenceRepository;
    private final NucleiFindingCweRepository nucleiFindingCweRepository;

    public NucleiFindingServiceImpl(
            NucleiFindingRepository nucleiFindingRepository,
            NucleiEvidenceRepository nucleiEvidenceRepository,
            NucleiFindingCweRepository nucleiFindingCweRepository
    ) {
        this.nucleiFindingRepository = nucleiFindingRepository;
        this.nucleiEvidenceRepository = nucleiEvidenceRepository;
        this.nucleiFindingCweRepository = nucleiFindingCweRepository;
    }


    @Override
    public NucleiFindingDetailResponse getDetailNucleiFinding(Long nucleiFindingId) throws ResponseStatusException {
        NucleiFinding finding = nucleiFindingRepository.findById(nucleiFindingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Not found this vulnerability"
                ));

        NucleiFindingDetailResponse res = new NucleiFindingDetailResponse();
        res.setId(finding.getNucleiFindingId());
        res.setTemplateId(finding.getTemplateId());
        res.setName(finding.getName());
        res.setSeverity(finding.getSeverity());
        res.setDescription(finding.getDescription());
        res.setMatchedAt(finding.getMatchedAt());

        List<NucleiEvidenceResponse> evidences =
            nucleiEvidenceRepository.findByNucleiFinding(finding)
                    .stream()
                    .map(ev -> {
                        NucleiEvidenceResponse e = new NucleiEvidenceResponse();
                        e.setType(ev.getType());
                        e.setCommand(ev.getCommand());
                        e.setResources(ev.getResources());
                        return e;
                    })
                    .toList();

        res.setEvidences(evidences);

        List<CWEResponse> cwe =
                nucleiFindingCweRepository.findByFinding(finding)
                        .stream()
                        .map(fc -> {
                            CWEResponse c = new CWEResponse();
                            c.setCweId(fc.getCwe().getCweId());
                            c.setName(fc.getCwe().getCweName());
                            c.setDescription(fc.getCwe().getShortDescription());
                            return c;
                        })
                        .toList();

        res.setCwe(cwe);

        return res;

    }
}
