package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.SCAN_STATUS;
import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.model.TargetApplication;
import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.repository.ScanRepository;
import com.hust.soict.vulntracer.repository.TargetApplicationRepository;
import com.hust.soict.vulntracer.repository.UserRepository;
import com.hust.soict.vulntracer.request.FromUserScanRequest;
import com.hust.soict.vulntracer.service.ScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ScanServiceImpl implements ScanService {
    private final ScanRepository scanRepository;
    private final UserRepository userRepository;
    private final TargetApplicationRepository targetApplicationRepository;

    @Autowired
    public ScanServiceImpl(
            ScanRepository scanRepository,
            UserRepository userRepository,
            TargetApplicationRepository targetApplicationRepository
    ) {
        this.scanRepository = scanRepository;
        this.userRepository = userRepository;
        this.targetApplicationRepository = targetApplicationRepository;
    }

    @Override
    public Scan createScan(FromUserScanRequest request) throws Exception {
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

        TargetApplication target = targetApplicationRepository.findByApplicationUrl(request.getTargetUrl());
        if (target == null) {
            target = new TargetApplication();
            target.setApplicationUrl(request.getTargetUrl());
            target.setApplicationName(request.getTargetName());
            target.setApplicationType(request.getTargetType());
            target.setApplicationDescription(request.getTargetDescription());
            target.setApplicationStatus("NEW");
            target.setApplicationCreatedAt(LocalDateTime.now());
            target.setApplicationUpdatedAt(LocalDateTime.now());

            target = targetApplicationRepository.save(target);
        }

        Scan scan = new Scan();
        scan.setUser(user);
        scan.setTargetApplication(target);
        scan.setStatus(SCAN_STATUS.PENDING);
        scan.setCreateAt(LocalDateTime.now());
        scan.setUpdateAt(LocalDateTime.now());
        scan.setStartTime(LocalDateTime.now());
        scan.setToolNames(request.getScanTools());

        Scan savedScan = scanRepository.save(scan);

        executeScanOnRails(savedScan);
        return savedScan;
    }

    public
}
