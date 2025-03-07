package com.shitcode.demo1.service;

import com.shitcode.demo1.entity.RegistrationToken;

public interface RegistrationTokenService {
    RegistrationToken createToken(Long userId);

    RegistrationToken resentToken(Long userId);

    RegistrationToken findByToken(String token);

    RegistrationToken validToken(String token);

    void deleteToken(String token);
}