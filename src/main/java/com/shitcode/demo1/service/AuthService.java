package com.shitcode.demo1.service;

import com.shitcode.demo1.dto.AuthDTO;
import com.shitcode.demo1.dto.GenericDTO;
import com.shitcode.demo1.dto.SpringUserDTO;

public interface AuthService {
    AuthDTO.LoginResponse login(AuthDTO.LoginRequest request);

    SpringUserDTO.Response signUp(SpringUserDTO.UserRequest request);

    GenericDTO.Response activeUserAccount(String token);

}
