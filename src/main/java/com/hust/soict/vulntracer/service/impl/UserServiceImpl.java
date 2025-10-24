package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.USER_ROLE;
import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.repository.UserRepository;
import com.hust.soict.vulntracer.request.RegisterRequest;
import com.hust.soict.vulntracer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setHashedPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(USER_ROLE.USER);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
}
