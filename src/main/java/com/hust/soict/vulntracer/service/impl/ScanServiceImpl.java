package com.hust.soict.vulntracer.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.soict.vulntracer.model.*;
import com.hust.soict.vulntracer.repository.*;
import com.hust.soict.vulntracer.request.CallbackRequest;
import com.hust.soict.vulntracer.request.CreateScanRequest;
import com.hust.soict.vulntracer.request.ScanToolRequest;
import com.hust.soict.vulntracer.response.*;
import com.hust.soict.vulntracer.service.ScanDispatcherService;
import com.hust.soict.vulntracer.service.ScanResultMapper;
import com.hust.soict.vulntracer.service.ScanService;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ScanServiceImpl implements ScanService {
    private final ScanRepository scanRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ScanDispatcherService scanDispatcherService;
    private final NucleiFindingRepository nucleiFindingRepository;
    private final CWERepository cweRepository;
    private final NucleiFindingCweRepository nucleiFindingCweRepository;
    private final NucleiEvidenceRepository nucleiEvidenceRepository;
    private final ZapFindingRepository zapFindingRepository;
    private final ZapEvidenceRepository zapEvidenceRepository;
    private final ZapFindingCweRepository zapFindingCweRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ScanServiceImpl(
            ScanRepository scanRepository,
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            ScanDispatcherService scanDispatcherService,
            NucleiFindingRepository nucleiFindingRepository,
            CWERepository cweRepository,
            NucleiFindingCweRepository nucleiFindingCweRepository,
            NucleiEvidenceRepository nucleiEvidenceRepository,
            ZapFindingRepository zapFindingRepository,
            ZapEvidenceRepository zapEvidenceRepository,
            ZapFindingCweRepository zapFindingCweRepository,
            ObjectMapper objectMapper
    ) {
        this.scanRepository = scanRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.scanDispatcherService = scanDispatcherService;
        this.nucleiFindingRepository = nucleiFindingRepository;
        this.cweRepository = cweRepository;
        this.nucleiFindingCweRepository = nucleiFindingCweRepository;
        this.nucleiEvidenceRepository = nucleiEvidenceRepository;
        this.zapFindingRepository = zapFindingRepository;
        this.zapEvidenceRepository = zapEvidenceRepository;
        this.zapFindingCweRepository = zapFindingCweRepository;
        this.objectMapper = objectMapper;
    }


    @Override
    public ScanResponse  createScan(CreateScanRequest request, String username) throws ResponseStatusException {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "request is null");
        }
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found"
            );
        }

        Application application  = applicationRepository.findByApplicationUrl(request.getApplicationUrl());
        if (application  == null) {
            application = new Application();
            application.setUser(user);
            application.setApplicationUrl(request.getApplicationUrl());
            application.setApplicationName(request.getApplicationName());
            application.setApplicationType(request.getApplicationType());
            application.setApplicationDescription(request.getApplicationDescription());
            application.setApplicationStatus("NEW");
            application.setApplicationCreatedAt(LocalDateTime.now());
            application.setApplicationUpdatedAt(LocalDateTime.now());

            application = applicationRepository.save(application);
        }

        Scan scan = new Scan();
        scan.setUser(user);
        scan.setApplication(application);
        scan.setStatus(SCAN_STATUS.PENDING);
        scan.setStartTime(LocalDateTime.now());

        return getScanResponse(request, scan);
    }

    @Override
    public List<ScanResponse> getScansByApplication(Long applicationId) throws ResponseStatusException {
        return scanRepository.findByApplication_ApplicationId(applicationId)
                .stream()
                .map(this::toScanResponse)
                .toList();
    }

    @Override
    public List<ScanResponse> getAllMyScan(String username) throws ResponseStatusException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
             throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found");
        }
        return scanRepository.findByUser_UserId(user.getUserId())
                .stream()
                .map(this::toScanResponse)
                .toList();
    }

    @Override
    public ScanResponse addScan(Long applicationId, CreateScanRequest request, String username) throws ResponseStatusException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found");
        }
        Application application = applicationRepository.findByApplicationId(applicationId);
        if (application == null) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Application not found"
        );
        Scan scan = new Scan();
        scan.setUser(user);
        scan.setApplication(application);
        scan.setStatus(SCAN_STATUS.PENDING);
        return getScanResponse(request, scan);
    }

    @Override
    @Transactional
    public void handleCallback(CallbackRequest req) throws ResponseStatusException {
        Scan scan = scanRepository.findById(req.getScanId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scan not found"));
        scan.setStatus(SCAN_STATUS.valueOf(req.getStatus()));
        scan.setCompletedAt(parseTime(req.getCompletedAt()));

        if (req.getResults().containsKey("nuclei")) {
            CallbackRequest.NucleiResultDto nuclei = objectMapper.convertValue(
                    req.getResults().get("nuclei"),
                    CallbackRequest.NucleiResultDto.class
            );

            saveScanSummary(scan, nuclei.getSummary());
            processNucleiFindings(scan, nuclei);
        }

        else if (req.getResults().containsKey("zap")) {
            CallbackRequest.ZapResultDto zap = objectMapper.convertValue(
                    req.getResults().get("zap"),
                    CallbackRequest.ZapResultDto.class
            );

            saveScanSummary(scan, zap.getSummary());
            processZapFindings(scan, zap);
        }

        scanRepository.save(scan);
    }

    private void saveScanSummary(Scan scan, CallbackRequest.SummaryDto summary) {
        if (summary != null) {
            scan.setTotal(summary.getTotal());
            scan.setCritical(summary.getCritical() != null ? summary.getCritical() : 0);
            scan.setHigh(summary.getHigh());
            scan.setMedium(summary.getMedium());
            scan.setLow(summary.getLow());
            scan.setInfo(summary.getInfo());
        }
    }

    private void processNucleiFindings(Scan scan, CallbackRequest.NucleiResultDto nuclei) {
        if (nuclei != null && nuclei.getVulnerabilities() != null) {
            for (CallbackRequest.NucleiVulnerabilityDto vuln : nuclei.getVulnerabilities()) {

                NucleiFinding finding = new NucleiFinding();
                finding.setScan(scan);
                finding.setTemplateId(vuln.getTemplate_id());
                finding.setName(vuln.getName());
                finding.setSeverity(vuln.getSeverity());
                finding.setDescription(vuln.getDescription());
                finding.setMatchedAt(vuln.getMatchedAt());

                finding = nucleiFindingRepository.save(finding);

                if (vuln.getCweIds() != null && !vuln.getCweIds().isEmpty()) {
                    for (String rawCweId : vuln.getCweIds()) {

                        String normalizedCweId = normalizeCweId(rawCweId);

                        if (normalizedCweId == null) continue;

                        CWE cwe = cweRepository.findById(normalizedCweId).orElse(null);

                        if (cwe != null) {
                            NucleiFindingCWE mapping = new NucleiFindingCWE();
                            mapping.setFinding(finding);
                            mapping.setCwe(cwe);
                            nucleiFindingCweRepository.save(mapping);
                        }
                    }
                }


                if (vuln.getEvidence() != null) {
                    NucleiEvidence evidence = new NucleiEvidence();
                    evidence.setNucleiFinding(finding);
                    evidence.setType(vuln.getEvidence().getType());
                    evidence.setCommand(vuln.getEvidence().getCommand());
                    evidence.setResources(vuln.getEvidence().getResources());

                    nucleiEvidenceRepository.save(evidence);
                }
            }
        }
    }

    private void processZapFindings(Scan scan, CallbackRequest.ZapResultDto zapResult) {
        if (zapResult.getVulnerabilities() == null) return;

        for (CallbackRequest.ZapVulnerabilityDto vuln : zapResult.getVulnerabilities()) {

            ZapFinding finding = new ZapFinding();
            finding.setScan(scan);
            finding.setPluginId(vuln.getPluginId());
            finding.setName(vuln.getName());
            finding.setSeverity(vuln.getSeverity());
            finding.setConfidence(vuln.getConfidence());
            finding.setDescription(vuln.getDescription());
            finding.setSolution(vuln.getSolution());
            finding.setWascId(vuln.getWascId());
            finding.setCweId(vuln.getCweId());

            finding = zapFindingRepository.save(finding);

            if (vuln.getCweId() != null && !vuln.getCweId().isEmpty()) {
                Integer cweNum = Integer.parseInt(vuln.getCweId());
                CWE cwe = cweRepository.findByCweNum(cweNum);

                if (cwe != null) {
                    ZapFindingCWE mapping = new ZapFindingCWE();
                    mapping.setZapFinding(finding);
                    mapping.setCwe(cwe);
                    zapFindingCweRepository.save(mapping);
                }
            }

            if (vuln.getEvidence() != null) {
                for (CallbackRequest.ZapEvidenceDto eviDto : vuln.getEvidence()) {
                    ZapEvidence evidence = new ZapEvidence();
                    evidence.setZapFinding(finding);
                    evidence.setUri(eviDto.getUri());
                    evidence.setMethod(eviDto.getMethod());
                    evidence.setParam(eviDto.getParam());
                    evidence.setEvidence(eviDto.getEvidence());

                    zapEvidenceRepository.save(evidence);
                }
            }
        }
    }

    @Override
    public ScanResultResponse<?> getScanResult(Long scanId, String username) throws ResponseStatusException {
        Scan scan = scanRepository.findById(scanId)
                .orElseThrow(() -> new RuntimeException("Scan not found"));

        if (!scan.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Forbidden");
        }
        List<NucleiFinding> findings =
                nucleiFindingRepository.findByScan(scan);
        List<NucleiFindingResponse> findingResponses =
                findings.stream()
                        .map(this::mapFinding)
                        .toList();

        List<ZapFinding> zapFindings = zapFindingRepository.findByScan(scan);
        List<ZapFindingResponse> zapFindingResponses = zapFindings.stream()
                .map(this::toZapFindingResponse)
                .toList();

        return ScanResultMapper.toResponse(scan, findingResponses, zapFindingResponses);
    }

    private LocalDateTime parseTime(String completedAt) {
        if (completedAt == null) return null;
        return OffsetDateTime.parse(completedAt).toLocalDateTime();
    }


    @NonNull
    private ScanResponse getScanResponse(CreateScanRequest request, Scan scan) {
        List<String> toolNames = request.getScanTools()
                .stream()
                .map(ScanToolRequest::getName)
                .toList();

        scan.setScanTools(toolNames);
        scan = scanRepository.save(scan);
        ScanResponse scanResponse = scanDispatcherService.sendToScanService(scan);

        scan.setStatus(scanResponse.getStatus());
        scan.setStartTime(LocalDateTime.now());
        scan.setStatus(scanResponse.getStatus());
        scanRepository.save(scan);

        return toScanResponse(scan);
    }

    private ScanResponse toScanResponse(Scan scan) {
        ScanResponse scanResponse = new ScanResponse();
        scanResponse.setScanId(scan.getScanId().toString());
        scanResponse.setStatus(scan.getStatus());
        scanResponse.setStartedAt(scan.getStartTime());
        return scanResponse;
    }

    private String normalizeCweId(String rawCweId) {
        if (rawCweId == null || rawCweId.isBlank()) return null;
        if (rawCweId.startsWith("CWE-")) {
            return rawCweId;
        }
        return "CWE-" + rawCweId.trim();
    }

    private NucleiFindingResponse mapFinding(NucleiFinding finding) {

        NucleiFindingResponse res = new NucleiFindingResponse();

        res.setId(finding.getNucleiFindingId());
        res.setTemplateId(finding.getTemplateId());
        res.setName(finding.getName());
        res.setSeverity(finding.getSeverity());
        res.setMatchedAt(finding.getMatchedAt());
        return res;
    }

    public ZapFindingResponse toZapFindingResponse(ZapFinding finding) {
        ZapFindingResponse res = new ZapFindingResponse();
        res.setZapFindingId(finding.getZapFindingId());
        res.setPluginId(finding.getPluginId());
        res.setName(finding.getName());
        res.setSeverity(finding.getSeverity());
        return res;
    }
}
