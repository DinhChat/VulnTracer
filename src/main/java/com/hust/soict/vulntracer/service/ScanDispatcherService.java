package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.SCAN_STATUS;
import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.request.ScanServiceRequest;
import com.hust.soict.vulntracer.response.ScanResponse;
import com.hust.soict.vulntracer.response.ScanServiceRawResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class ScanDispatcherService {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://192.168.156.32:3000";

    public ScanDispatcherService(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .rootUri(BASE_URL)
                .build();
    }

    public ScanResponse sendToScanService(Scan scan) {
        ScanServiceRequest request = buildScanServiceRequest(scan);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<ScanServiceRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<ScanServiceRawResponse> response = restTemplate.exchange(
                    "/scans/start",
                    HttpMethod.POST,
                    entity,
                    ScanServiceRawResponse.class
            );

            HttpStatus statusCode = (HttpStatus) response.getStatusCode();

            if (statusCode.is2xxSuccessful() || statusCode == HttpStatus.ACCEPTED) {
                ScanServiceRawResponse rawBody = response.getBody();
                if (rawBody != null) {
                    ScanResponse scanResponse = mapToScanResponse(rawBody);
                    log.info("Scan {} dispatched successfully (status: {}, scan_id: {})",
                            scan.getScanId(),
                            scanResponse.getStatus(),
                            scanResponse.getScanId());

                    return scanResponse;
                }
            }

            log.error("Unexpected response status from scan service: {}", statusCode);
            throw new RuntimeException("Scan service returned unexpected status: " + statusCode);

        } catch (Exception e) {
            log.error("Failed to dispatch scan {}", scan.getScanId(), e);
            throw new RuntimeException("Failed to dispatch scan to scan service", e);
        }
    }

    private ScanServiceRequest buildScanServiceRequest(Scan scan) {
        return ScanServiceRequest.builder()
                .scan_id(scan.getScanId())
                .target_url(scan.getApplication().getApplicationUrl())
                .scan_tools(scan.getScanTools())
                .callback_url("http://192.168.156.185:8080/scan/callback")
                .scan_parameters(Map.of())
                .build();
    }

    private LocalDateTime parseTime(String queuedAt) {
        if (queuedAt == null) return null;
        return OffsetDateTime.parse(queuedAt).toLocalDateTime();
    }

    private ScanResponse mapToScanResponse(ScanServiceRawResponse raw) {
        return new ScanResponse(
                raw.getScanId(),
                SCAN_STATUS.valueOf(raw.getStatus()),
                raw.getMessage(),
                null,
                parseTime(raw.getQueuedAt())
        );
    }
}
