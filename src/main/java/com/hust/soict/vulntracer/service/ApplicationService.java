package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.request.CreateApplicationRequest;
import com.hust.soict.vulntracer.response.ApplicationResponse;

import java.util.List;

public interface ApplicationService {
    List<ApplicationResponse> getAllApplication();
    ApplicationResponse createApplication(CreateApplicationRequest request) throws Exception;
}
