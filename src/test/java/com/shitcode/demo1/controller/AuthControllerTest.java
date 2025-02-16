package com.shitcode.demo1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shitcode.demo1.dto.AuthDTO;
import com.shitcode.demo1.service.AuthService;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    @Order(1)
    @DisplayName("Should return access and refresh token after successful login")
    void shouldReturnAccessAndRefreshTokenAfterLogin() throws Exception {
        // Given
        AuthDTO.Request request = AuthDTO.Request.builder()
                .email("test@email.com")
                .password("Abc123@#")
                .build();
        AuthDTO.Response response = AuthDTO.Response.builder()
                .accessToken("Access Token")
                .refreshToken("Refresh Token")
                .build();
        // When
        when(authService.login(any(AuthDTO.Request.class))).thenReturn(response);
        // Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.access-token").value("Access Token"))
                .andExpect(jsonPath("$.refresh-token").value("Refresh Token"));
    }
}
