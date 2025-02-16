package com.shitcode.demo1.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.AuthDTO;
import com.shitcode.demo1.jwt.JwtService;
import com.shitcode.demo1.service.AuthService;
import com.shitcode.demo1.utils.LoggingModel;

@Service
@LogCollector(loggingModel =  LoggingModel.SERVICE)
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthDTO.Response login(AuthDTO.Request request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        return AuthDTO.Response.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
