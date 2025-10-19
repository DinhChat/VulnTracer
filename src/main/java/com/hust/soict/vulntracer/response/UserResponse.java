package com.hust.soict.vulntracer.response;

import com.hust.soict.vulntracer.model.USER_ROLE;
import lombok.Data;

@Data
public class UserResponse {
    private String username;
    private String jwtToken;
    private USER_ROLE userRole;
    private String message;
}
