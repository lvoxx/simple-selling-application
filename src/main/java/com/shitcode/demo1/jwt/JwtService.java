package com.shitcode.demo1.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.google.errorprone.annotations.DoNotMock;
import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.properties.ClientConfigData;
import com.shitcode.demo1.properties.JwtConfigData;
import com.shitcode.demo1.utils.LoggingModel;

@Service
@Scope("application")
@DoNotMock
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtConfigData jwtConfigData;
    private final ClientConfigData clientConfigData;

    public JwtService(JwtEncoder jwtEncoder, JwtConfigData jwtConfigData, ClientConfigData clientConfigData) {
        this.jwtEncoder = jwtEncoder;
        this.jwtConfigData = jwtConfigData;
        this.clientConfigData = clientConfigData;
    }

    @LogCollector(extraMessage = "JWT Access Token generated.", loggingModel = LoggingModel.SERVICE)
    public String generateAccessToken(Authentication authentication) {
        String refreshToken = generateToken(authentication, jwtConfigData.getAccessTokenExpiration());
        return refreshToken;
    }

    @LogCollector(extraMessage = "JWT Refresh Token generated.", loggingModel = LoggingModel.SERVICE)
    public String generateRefreshToken(Authentication authentication) {
        String refreshToken = generateToken(authentication, jwtConfigData.getRefreshTokenExpiration());
        return refreshToken;
    }

    private String generateToken(@NonNull Authentication authentication, Long expirationDate) {
        Instant now = Instant.now();

        String scope = Optional.ofNullable(authentication.getAuthorities())
                .orElse(Collections.emptyList())
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .audience(List.of(clientConfigData.getName()))
                .issuer(clientConfigData.getName())
                .issuedAt(now)
                .expiresAt(now.plus(expirationDate, ChronoUnit.MILLIS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
