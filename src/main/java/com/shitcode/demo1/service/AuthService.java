package com.shitcode.demo1.service;

import com.shitcode.demo1.dto.AuthDTO;

public interface AuthService {
    AuthDTO.Response login(AuthDTO.Request request);
}
