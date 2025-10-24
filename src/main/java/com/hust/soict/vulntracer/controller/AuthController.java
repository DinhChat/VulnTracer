package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.request.LoginRequest;
import com.hust.soict.vulntracer.request.RegisterRequest;
import com.hust.soict.vulntracer.response.UserResponse;
import com.hust.soict.vulntracer.security.JwtProvider;
import com.hust.soict.vulntracer.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtProvider jwtProvider,  UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestBody RegisterRequest registerRequest
    ) throws Exception {
        User savedUser = userService.registerUser(registerRequest);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getUsername(),
                savedUser.getHashedPassword(),
                List.of(new SimpleGrantedAuthority(savedUser.getRole().toString()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(savedUser.getUsername());
        userResponse.setJwtToken(jwtProvider.generateJwtToken(authentication));
        userResponse.setMessage("Registered Successfully");
        userResponse.setRole(savedUser.getRole());

        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody LoginRequest loginRequest
    ) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(userDetails.getUsername());

        if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            userResponse.setJwtToken(jwtProvider.generateJwtToken(authentication));
            userResponse.setMessage("Login Successfully!");
        } else {
            userResponse.setMessage("Login Fail!");
        }

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

}
