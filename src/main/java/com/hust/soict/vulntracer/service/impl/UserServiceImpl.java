package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.USER_ROLE;
import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.repository.UserRepository;
import com.hust.soict.vulntracer.request.LoginRequest;
import com.hust.soict.vulntracer.request.RegisterRequest;
import com.hust.soict.vulntracer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

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

    @Override
    public User loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        }

        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getHashedPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid username or password");
        }

        return user;
    }

}
