package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.*;
import com.hust.soict.vulntracer.repository.*;
import com.hust.soict.vulntracer.request.CallbackRequest;
import com.hust.soict.vulntracer.request.CreateScanRequest;
import com.hust.soict.vulntracer.request.ScanToolRequest;
import com.hust.soict.vulntracer.response.ScanResponse;
import com.hust.soict.vulntracer.service.ScanDispatcherService;
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

    @Autowired
    public ScanServiceImpl(
            ScanRepository scanRepository,
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            ScanDispatcherService scanDispatcherService,
            NucleiFindingRepository nucleiFindingRepository,
            CWERepository cweRepository,
            NucleiFindingCweRepository nucleiFindingCweRepository,
            NucleiEvidenceRepository nucleiEvidenceRepository
    ) {
        this.scanRepository = scanRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.scanDispatcherService = scanDispatcherService;
        this.nucleiFindingRepository = nucleiFindingRepository;
        this.cweRepository = cweRepository;
        this.nucleiFindingCweRepository = nucleiFindingCweRepository;
        this.nucleiEvidenceRepository = nucleiEvidenceRepository;
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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Scan not found"));
        scan.setStatus(SCAN_STATUS.valueOf(req.getStatus()));
        scan.setCompletedAt(parseTime(req.getCompletedAt()));

        CallbackRequest.NucleiResultDto nuclei = req.getResults() != null
                ? req.getResults().get("nuclei")
                : null;

        if (nuclei != null) {
            if (nuclei.getSummary() != null) {
                scan.setTotal(nuclei.getSummary().getTotal());
                scan.setCritical(nuclei.getSummary().getCritical());
                scan.setHigh(nuclei.getSummary().getHigh());
                scan.setMedium(nuclei.getSummary().getMedium());
                scan.setLow(nuclei.getSummary().getLow());
                scan.setInfo(nuclei.getSummary().getInfo());
            }
        }

        assert nuclei != null;
        if (nuclei.getVulnerabilities() != null) {
            for (CallbackRequest.VulnerabilityDto vuln : nuclei.getVulnerabilities()) {

                NucleiFinding finding = new NucleiFinding();
                finding.setScan(scan);
                finding.setTemplateId(vuln.getTemplate_id());
                finding.setName(vuln.getName());
                finding.setSeverity(vuln.getSeverity());
                finding.setDescription(vuln.getDescription());
                finding.setMatchedAt(vuln.getMatchedAt());

                finding = nucleiFindingRepository.save(finding);

                if (vuln.getCweIds() != null && !vuln.getCweIds().isEmpty()) {
                    for (String cweId : vuln.getCweIds()) {
                        CWE cwe = cweRepository.findById(cweId).orElse(null);
                        if (cwe != null) {
                            NucleiFindingCWE mapping = new NucleiFindingCWE();
                            mapping.setFinding(finding);
                            mapping.setCwe(cwe);
                            nucleiFindingCweRepository.save(mapping);
                        }
                    }
                }

                // --- Evidence ---
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

        scanRepository.save(scan);
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
}
