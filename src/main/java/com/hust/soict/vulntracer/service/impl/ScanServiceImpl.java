package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.SCAN_STATUS;
import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.model.Application;
import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.repository.ScanRepository;
import com.hust.soict.vulntracer.repository.ApplicationRepository;
import com.hust.soict.vulntracer.repository.UserRepository;
import com.hust.soict.vulntracer.request.CreateScanRequest;
import com.hust.soict.vulntracer.request.ScanToolRequest;
import com.hust.soict.vulntracer.response.ScanResponse;
import com.hust.soict.vulntracer.service.ScanDispatcherService;
import com.hust.soict.vulntracer.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScanServiceImpl implements ScanService {
    private final ScanRepository scanRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ScanDispatcherService scanDispatcherService;

    @Autowired
    public ScanServiceImpl(
            ScanRepository scanRepository,
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            ScanDispatcherService scanDispatcherService
    ) {
        this.scanRepository = scanRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.scanDispatcherService = scanDispatcherService;
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
        scan.setCreateAt(LocalDateTime.now());
        scan.setUpdateAt(LocalDateTime.now());
        scan.setStartTime(LocalDateTime.now());

        List<String> toolNames = request.getScanTools()
                .stream()
                .map(ScanToolRequest::getName)
                .toList();

        scan.setScanTools(toolNames);
        scan = scanRepository.save(scan);
        ScanResponse scanResponse = scanDispatcherService.sendToScanService(scan);

        scan.setStatus(scanResponse.getStatus());
        scan.setStartTime(scanResponse.getQueuedAt());
        scan.setUpdateAt(LocalDateTime.now());
        scanRepository.save(scan);

        return toScanResponse(scan);
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
        scan.setCreateAt(LocalDateTime.now());
        List<String> toolNames = request.getScanTools()
                .stream()
                .map(ScanToolRequest::getName)
                .toList();

        scan.setScanTools(toolNames);
        scan = scanRepository.save(scan);
        ScanResponse scanResponse = scanDispatcherService.sendToScanService(scan);

        scan.setStatus(scanResponse.getStatus());
        scan.setStartTime(scanResponse.getQueuedAt());
        scan.setUpdateAt(LocalDateTime.now());
        scanRepository.save(scan);

        return toScanResponse(scan);
    }

    private ScanResponse toScanResponse(Scan scan) {
        ScanResponse scanResponse = new ScanResponse();
        scanResponse.setScanId(scan.getScanId().toString());
        scanResponse.setStatus(scan.getStatus());
        scanResponse.setQueuedAt(scan.getCreateAt());

        return scanResponse;
    }
}
