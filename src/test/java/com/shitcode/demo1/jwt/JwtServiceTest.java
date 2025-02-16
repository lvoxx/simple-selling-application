package com.shitcode.demo1.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import com.shitcode.demo1.entity.SpringUser;
import com.shitcode.demo1.properties.ClientConfigData;
import com.shitcode.demo1.security.SpringUserDetails;

@DisplayName("Jwt Service Tests")
@SpringBootTest
@Tags({
                @Tag("Service"), @Tag("Jwt"), @Tag("No Mock")
})
public class JwtServiceTest {
        @Autowired
        JwtService jwtService;

        @Autowired
        JwtDecoder jwtDecoder;

        @Autowired
        ClientConfigData clientConfigData;

        private Authentication authentication;

        @BeforeEach
        void setUp() {
                SpringUser user = SpringUser.builder()
                                .email("test@email.com")
                                .password("Test@#Password123")
                                .firstName("Test")
                                .lastName("Account")
                                .locked(false)
                                .enabled(true)
                                .points(BigDecimal.valueOf(10))
                                .roles(List.of("ADMIN SUPER-USER"))
                                .build();
                SpringUserDetails userDetails1 = new SpringUserDetails(user);
                authentication = new UsernamePasswordAuthenticationToken(
                                userDetails1,
                                "password",
                                userDetails1.getAuthorities());
        }

        @AfterEach
        void tearDown() {
                authentication = null;
        }

        @Test
        @DisplayName("Should return access token when authentication is provided")
        void shouldReturnAccessTokenWhenGivingAuthentication() {
                // When
                String result = jwtService.generateAccessToken(authentication);
                Jwt jwt = jwtDecoder.decode(result);
                // Then
                assertThat(result).isNotEmpty();
                assertThat(result)
                                .matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$")
                                .withFailMessage("Token format is invalid: %s", result);
                // Test claims
                assertThat(jwt.getClaim("scope").toString()).isEqualTo("ADMIN SUPER-USER")
                                .withFailMessage("Unexpected scope claims: %s", jwt.getClaim("scope").toString());
                // Test audi
                assertThat(jwt.getAudience().getFirst()).isEqualTo(clientConfigData.getName())
                                .withFailMessage("Unexpected audit: %s", jwt.getAudience());
        }

        @Test
        @DisplayName("Should return refresh token when authentication is provided")
        void shouldReturnRefreshTokenWhenGivingAuthentication() {
                // When
                String result = jwtService.generateRefreshToken(authentication);
                Jwt jwt = jwtDecoder.decode(result);
                // Then
                assertThat(result).isNotEmpty();
                assertThat(result)
                                .matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$")
                                .withFailMessage("Token format is invalid: %s", result);
                // Test claims
                assertThat(jwt.getClaim("scope").toString()).isEqualTo("ADMIN SUPER-USER")
                                .withFailMessage("Unexpected scope claims: %s", jwt.getClaim("scope").toString());
                // Test audi
                assertThat(jwt.getAudience().getFirst()).isEqualTo(clientConfigData.getName())
                                .withFailMessage("Unexpected audit: %s", jwt.getAudience());
        }

        @Test
        @DisplayName("Should not return access token when authentication is not provided")
        void shouldNotReturnAccessTokenWhenGivingUnAuthentication() {
        }

        @Test
        @DisplayName("Should not return refresh token when authentication is not provided")
        void shouldNotReturnRefreshTokenWhenGivingUnAuthentication() {
        }

}
