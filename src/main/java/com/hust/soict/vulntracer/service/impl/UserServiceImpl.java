package com.hust.soict.vulntracer.service.impl;

import com.hust.soict.vulntracer.model.USER_ROLE;
import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.repository.UserRepository;
import com.hust.soict.vulntracer.request.SignUpRequest;
import com.hust.soict.vulntracer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User findUserByEmail(String email) throws Exception {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found with email: " + email);
        }
    }

    @Override
    public User findUserByUsername(String username) throws Exception {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new Exception("User not found with username: " + username);
        }
    }

    @Override
    public User registerUser(SignUpRequest signUpRequest) throws Exception {
        if (userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setRole(USER_ROLE.USER);

        return userRepository.save(user);
    }
}
