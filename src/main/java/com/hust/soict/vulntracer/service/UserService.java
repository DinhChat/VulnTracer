package com.hust.soict.vulntracer.service;

import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.request.LoginRequest;
import com.hust.soict.vulntracer.request.RegisterRequest;

public interface UserService {
    User registerUser(RegisterRequest registerRequest) throws Exception;
    User loginUser(LoginRequest loginRequest);
}
