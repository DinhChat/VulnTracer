package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.Application;
import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.repository.ApplicationRepository;
import com.hust.soict.vulntracer.repository.UserRepository;
import com.hust.soict.vulntracer.request.CreateApplicationRequest;
import com.hust.soict.vulntracer.response.ApplicationResponse;
import com.hust.soict.vulntracer.service.ApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ApplicationResponse> getAllApplication(String username) throws ResponseStatusException {
        User user = userRepository.findByUsername(username);
        return applicationRepository.findAllTargetsWithLastScan(user);
    }

    @Override
    public ApplicationResponse createApplication(CreateApplicationRequest request) {

        Application application = new Application();

        application.setApplicationUrl(request.getApplicationUrl());
        application.setApplicationName(request.getApplicationName());
        application.setApplicationType(request.getApplicationType());
        application.setApplicationStatus(request.getApplicationStatus());
        application.setApplicationDescription(request.getApplicationDescription());
        application.setApplicationCreatedAt(LocalDateTime.now());
        application.setApplicationUpdatedAt(null);

        Application createdApplication = applicationRepository.save(application);

        return toAPplicationResponse(createdApplication);
    }

    private ApplicationResponse toAPplicationResponse(Application application) {
        ApplicationResponse applicationResponse = new ApplicationResponse();

        applicationResponse.setApplicationId(application.getApplicationId());
        applicationResponse.setApplicationUrl(application.getApplicationUrl());
        applicationResponse.setApplicationName(application.getApplicationName());
        applicationResponse.setApplicationType(application.getApplicationType());
        applicationResponse.setApplicationStatus(application.getApplicationStatus());
        applicationResponse.setApplicationCreatedAt(application.getApplicationCreatedAt());

        return applicationResponse;
    }
}
