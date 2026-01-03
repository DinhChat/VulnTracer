package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.response.NucleiFindingResponse;
import com.hust.soict.vulntracer.response.ScanInfoResponse;
import com.hust.soict.vulntracer.response.ScanResultResponse;
import com.hust.soict.vulntracer.response.VulnerabilitySummaryResponse;

import java.util.List;

public class ScanResultMapper {

    public static ScanResultResponse toResponse(
            Scan scan,
            List<NucleiFindingResponse> findings
    ) {
        ScanResultResponse res = new ScanResultResponse();

        ScanInfoResponse scanInfo = new ScanInfoResponse();
        scanInfo.setScanId(scan.getScanId());
        scanInfo.setApplicationName(scan.getApplication().getApplicationName());
        scanInfo.setStartTime(scan.getStartTime());
        scanInfo.setCompletedAt(scan.getCompletedAt());
        scanInfo.setStatus(scan.getStatus());
        res.setScan(scanInfo);

        VulnerabilitySummaryResponse summary = new VulnerabilitySummaryResponse();
        summary.setTotal(scan.getTotal());
        summary.setCritical(scan.getCritical());
        summary.setHigh(scan.getHigh());
        summary.setMedium(scan.getMedium());
        summary.setLow(scan.getLow());
        summary.setInfo(scan.getInfo());
        res.setSummary(summary);

        res.setVulnerabilities(findings);

        return res;
    }
}

