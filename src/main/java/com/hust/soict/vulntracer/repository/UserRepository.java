package com.hust.soict.vulntracer.repository;

import com.hust.soict.vulntracer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User findUserByEmail(String email);
    User findUserByUsername(String username);
}

