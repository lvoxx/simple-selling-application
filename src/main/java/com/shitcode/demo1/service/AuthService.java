package com.shitcode.demo1.service;

import com.shitcode.demo1.dto.AuthDTO;

public interface AuthService {
    AuthDTO.LoginResponse login(AuthDTO.LoginRequest request);
}
