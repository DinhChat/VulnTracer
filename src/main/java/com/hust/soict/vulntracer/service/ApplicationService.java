package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.request.CreateApplicationRequest;
import com.hust.soict.vulntracer.response.ApplicationResponse;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public interface ApplicationService {
    List<ApplicationResponse> getAllApplication(String username) throws ResponseStatusException;
    ApplicationResponse createApplication(CreateApplicationRequest request) throws Exception;
}
