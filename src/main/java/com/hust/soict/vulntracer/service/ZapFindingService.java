package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.response.ZapFindingDetailResponse;
import org.springframework.web.server.ResponseStatusException;

public interface ZapFindingService {
    ZapFindingDetailResponse getDetailZapFinding(Long zapFindingId) throws ResponseStatusException;
}
