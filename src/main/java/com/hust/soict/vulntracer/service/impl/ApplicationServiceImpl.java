package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.repository.ApplicationRepository;
import com.hust.soict.vulntracer.request.CreateApplicationRequest;
import com.hust.soict.vulntracer.response.ApplicationResponse;
import com.hust.soict.vulntracer.service.ApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository1) {
        this.applicationRepository = applicationRepository1;
    }

    @Override
    public List<ApplicationResponse> getAllApplication() {
        return applicationRepository.findAllTargetsWithLastScan();
    }

    @Override
    public ApplicationResponse createApplication(CreateApplicationRequest request) throws Exception {
        return null;
    }
}
