package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

