package com.shitcode.demo1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shitcode.demo1.dto.AuthDTO;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.AuthService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({
                @Tag("Controller"), @Tag("Mock")
})
@DisplayName("Auth controller tests with mocking")
// Import needed components
@Import({ RateLimiterConfigData.class })
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

        @Autowired
        ObjectMapper objectMapper;

        @Autowired
        MockMvc mockMvc;

        @MockitoBean
        AuthService authService;

        @MockitoBean
        ResponseService responseService;

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Before
        public void setup() {
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        @SuppressWarnings("unchecked")
        @Test
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
                when(responseService.mapping(any(Supplier.class), any(RateLimiterPlan.class)))
                                .thenReturn(ResponseEntity.ok().body(ResponseDTO.builder().data(response).build()));
                // Then
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.data.access-token").value("Access Token"))
                                .andExpect(jsonPath("$.data.refresh-token").value("Refresh Token"));
        }
}
