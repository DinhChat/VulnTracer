package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.Application;
import com.hust.soict.vulntracer.response.ApplicationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    @Query("""
        SELECT new com.hust.soict.vulntracer.response.ApplicationResponse(
            t.applicationId,
            t.applicationName,
            t.applicationUrl,
            t.applicationType,
            t.applicationStatus,
            t.applicationCreatedAt,
            MAX(s.startTime)
        )
        FROM Application t
        LEFT JOIN Scan s ON s.application = t
        GROUP BY t
    """)
    List<ApplicationResponse> findAllTargetsWithLastScan();
    Application findByApplicationUrl(String targetUrl);
}
