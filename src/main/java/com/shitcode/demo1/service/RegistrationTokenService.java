package com.shitcode.demo1.service;

import com.shitcode.demo1.entity.RegistrationToken;

public interface RegistrationTokenService {
    RegistrationToken createToken(Long userId);

    RegistrationToken revokeToken(Long userId);

    void deleteToken(String token);
}