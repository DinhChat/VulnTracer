package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.response.NucleiFindingDetailResponse;
import org.springframework.web.server.ResponseStatusException;

public interface NucleiFindingService {
    NucleiFindingDetailResponse getDetailNucleiFinding(Long nucleiFindingId) throws ResponseStatusException;
}
