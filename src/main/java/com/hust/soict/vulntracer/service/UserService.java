package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.request.SignUpRequest;

public interface UserService {
    public User findUserByEmail(String email) throws Exception;
    public User findUserByUsername(String username) throws Exception;
    public User registerUser(SignUpRequest signUpRequest) throws Exception;
}
