package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.TargetApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetApplicationRepository extends JpaRepository<TargetApplication, Long> {

    TargetApplication findByApplicationUrl(String targetUrl);
}
