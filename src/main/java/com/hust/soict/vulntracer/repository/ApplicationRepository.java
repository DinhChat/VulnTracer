package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.Application;
import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.response.ApplicationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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
        WHERE t.user = :user
        GROUP BY t
    """)
    List<ApplicationResponse> findAllTargetsWithLastScan(@Param("user") User user);
    Application findByApplicationUrl(String targetUrl);
    Application findByApplicationId (Long applicationId);
}
