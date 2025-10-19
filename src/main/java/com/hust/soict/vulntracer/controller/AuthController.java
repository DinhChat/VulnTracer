package com.hust.soict.vulntracer.controller;

import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.request.LoginRequest;
import com.hust.soict.vulntracer.request.SignUpRequest;
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
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthController {
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserDetailsService userDetailsService,
                          UserService userService,
                          JwtProvider jwtProvider,
                          PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signUp(@RequestBody SignUpRequest req) throws Exception {
        User savedUser = userService.registerUser(req);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getUsername(),
                savedUser.getPassword(),
                List.of(new SimpleGrantedAuthority(savedUser.getRole().toString()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserResponse res = new UserResponse();
        res.setUsername(savedUser.getUsername());
        res.setJwtToken(jwtProvider.generateJwtToken(authentication));
        res.setMessage("Sign up successfully!");
        res.setUserRole(savedUser.getRole());

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest req) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getUsername());
        UserResponse res = new UserResponse();
        res.setUsername(req.getUsername());

        if (passwordEncoder.matches(req.getPassword(), userDetails.getPassword())) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            res.setJwtToken(jwtProvider.generateJwtToken(authentication));
            res.setMessage("Login Successfully!");
        } else {
            res.setMessage("Login Fail!");
        }

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
