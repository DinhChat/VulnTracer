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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private final JwtProvider jwtProvider;
    private final UserService userService;

    public AuthController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
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
        User user = userService.loginUser(loginRequest);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getHashedPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setRole(user.getRole());
        userResponse.setJwtToken(jwtProvider.generateJwtToken(authentication));
        userResponse.setMessage("Login Successfully");

        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

}
