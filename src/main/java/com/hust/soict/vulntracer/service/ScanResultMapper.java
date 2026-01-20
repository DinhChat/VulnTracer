package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.Scan;
import com.hust.soict.vulntracer.response.*;

import java.util.Collections;
import java.util.List;

public class ScanResultMapper {

    public static ScanResultResponse<?> toResponse(
            Scan scan,
            List<NucleiFindingResponse> nucleiFindings,
            List<ZapFindingResponse> zapFindings
    ) {
        ScanInfoResponse scanInfo = new ScanInfoResponse();
        scanInfo.setScanId(scan.getScanId());
        scanInfo.setApplicationName(scan.getApplication().getApplicationName());
        scanInfo.setStartTime(scan.getStartTime());
        scanInfo.setCompletedAt(scan.getCompletedAt());
        scanInfo.setStatus(scan.getStatus());

        VulnerabilitySummaryResponse summary = new VulnerabilitySummaryResponse();
        summary.setTotal(scan.getTotal());
        summary.setCritical(scan.getCritical());
        summary.setHigh(scan.getHigh());
        summary.setMedium(scan.getMedium());
        summary.setLow(scan.getLow());
        summary.setInfo(scan.getInfo());

        if (nucleiFindings != null && !nucleiFindings.isEmpty()) {
            ScanResultResponse<NucleiFindingResponse> res = new ScanResultResponse<>();
            res.setScan(scanInfo);
            res.setSummary(summary);
            res.setVulnerabilities(nucleiFindings);
            return res;
        }
        else if (zapFindings != null && !zapFindings.isEmpty()) {
            ScanResultResponse<ZapFindingResponse> res = new ScanResultResponse<>();
            res.setScan(scanInfo);
            res.setSummary(summary);
            res.setVulnerabilities(zapFindings);
            return res;
        }

        ScanResultResponse<Object> res = new ScanResultResponse<>();
        res.setScan(scanInfo);
        res.setSummary(summary);
        res.setVulnerabilities(Collections.emptyList());
        return res;
    }

}

