package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.SCAN_STATUS;
import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.model.Application;
import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.repository.ScanRepository;
import com.hust.soict.vulntracer.repository.ApplicationRepository;
import com.hust.soict.vulntracer.repository.UserRepository;
import com.hust.soict.vulntracer.request.CreateScanRequest;
import com.hust.soict.vulntracer.response.ScanResponse;
import com.hust.soict.vulntracer.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScanServiceImpl implements ScanService {
    private final ScanRepository scanRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    @Autowired
    public ScanServiceImpl(
            ScanRepository scanRepository,
            UserRepository userRepository,
            ApplicationRepository applicationRepository
    ) {
        this.scanRepository = scanRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<ScanResponse> getScansByTarget(Long applicationId) {
        return scanRepository.findByApplication_ApplicationId(applicationId)
                .stream()
                .map(this::toScanResponse)
                .toList();
    }


    @Override
    public Scan createScan(CreateScanRequest request) throws Exception {
        if (request == null) {
            throw new Exception("request is null");
        }
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new Exception("username not found");
        }

        Application target = applicationRepository.findByApplicationUrl(request.getApplicationUrl());
        if (target == null) {
            target = new Application();
            target.setApplicationUrl(request.getApplicationUrl());
            target.setApplicationName(request.getApplicationName());
            target.setApplicationType(request.getApplicationType());
            target.setApplicationDescription(request.getApplicationDescription());
            target.setApplicationStatus("NEW");
            target.setApplicationCreatedAt(LocalDateTime.now());
            target.setApplicationUpdatedAt(LocalDateTime.now());

            target = applicationRepository.save(target);
        }

        Scan scan = new Scan();
        scan.setUser(user);
        scan.setApplication(target);
        scan.setStatus(SCAN_STATUS.PENDING);
        scan.setCreateAt(LocalDateTime.now());
        scan.setUpdateAt(LocalDateTime.now());
        scan.setStartTime(LocalDateTime.now());
        scan.setToolNames(request.getScanTools());

        Scan savedScan = scanRepository.save(scan);

//        executeScanOnRails(savedScan);
        return savedScan;
    }

}
