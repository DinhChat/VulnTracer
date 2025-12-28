package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.request.ScanServiceRequest;
import com.hust.soict.vulntracer.response.ScanResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
public class ScanDispatcherService {
    private final WebClient webClient;

    public ScanDispatcherService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://192.168.1.89:3000")
                .build();
    }

    public ScanResponse sendToScanService(Scan scan) {

        ScanServiceRequest request = buildScanServiceRequest(scan);

        return webClient.post()
                .uri("/scans/start")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ScanResponse.class)
                .doOnSuccess(res ->
                        log.info("Scan {} dispatched successfully", scan.getScanId())
                )
                .doOnError(err ->
                        log.error("Failed to dispatch scan {}", scan.getScanId(), err)
                )
                .block();
    }

    private ScanServiceRequest buildScanServiceRequest(Scan scan) {

        return ScanServiceRequest.builder()
                .scan_id(scan.getScanId())
                .target_url(scan.getApplication().getApplicationUrl())
                .scan_tools(scan.getScanTools())
                .callback_url("http://localhost:4000/api/scan/callback")
                .scan_parameters(Map.of())
                .build();
    }
}
