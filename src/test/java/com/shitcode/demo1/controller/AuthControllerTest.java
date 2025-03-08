package com.shitcode.demo1.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shitcode.demo1.dto.AuthDTO;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.exception.model.TokenExpiredException;
import com.shitcode.demo1.exception.model.UserDisabledException;
import com.shitcode.demo1.properties.FontendServerConfigData;
import com.shitcode.demo1.properties.MailingConfigData;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.AuthService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.service.SpringUserService;
import com.shitcode.demo1.utils.RateLimiterPlan;

import lombok.SneakyThrows;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tags({
                @Tag("Controller"), @Tag("Mock")
})
@DisplayName("Auth controller tests with mocking")
// Import needed components
@Import({ RateLimiterConfigData.class, FontendServerConfigData.class, MailingConfigData.class })
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

        @Autowired
        ObjectMapper objectMapper;

        @Autowired
        MockMvc mockMvc;

        @MockitoBean
        AuthService authService;

        @MockitoBean
        SpringUserService springUserService;

        @MockitoBean
        ResponseService responseService;

        @Autowired
        private WebApplicationContext webApplicationContext;

        @Autowired
        FontendServerConfigData fontendServerConfigData;

        @Autowired
        MailingConfigData mailingConfigData;

        @Before
        public void setup() {
                mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        }

        @TestConfiguration
        public static class LoadBeanContext {
                @Bean
                public MailingConfigData mailingConfigData() {
                        return new MailingConfigData();
                }

        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        @Test
        @DisplayName("Should return access and refresh token after successful login")
        void shouldReturnAccessAndRefreshTokenAfterLogin() {
                // Given
                AuthDTO.LoginRequest request = AuthDTO.LoginRequest.builder()
                                .email("test@email.com")
                                .password("Abc123@#")
                                .build();
                AuthDTO.LoginResponse response = AuthDTO.LoginResponse.builder()
                                .accessToken("Access Token")
                                .refreshToken("Refresh Token")
                                .build();
                // When
                when(authService.login(any(AuthDTO.LoginRequest.class))).thenReturn(response);
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
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

        @SuppressWarnings("unchecked")
        @SneakyThrows
        @Test
        @DisplayName("Should redirect to error page after throwing a disabled user exception")
        void shouldRedirectToErrorPagesAfterThrowingUserDisabledException_whenGettingActivationPath() {
                // Arrange
                String dynamicPath = mailingConfigData.getRegisterEmail().getPath();
                // Given
                String token = UUID.randomUUID().toString();
                // When
                when(authService.activeUserAccount(anyString())).thenThrow(new UserDisabledException("Dummy Error"));
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenThrow(new UserDisabledException("Dummy Error"));
                // Then
                mockMvc.perform(get("/auth" + dynamicPath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .param("token", token))
                                .andDo(print())
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl(fontendServerConfigData.getActive().get("disabled")));
        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        @Test
        @DisplayName("Should redirect to error page after throwing an expired token exception")
        void shouldRedirectToErrorPagesAfterThrowingExpiredTokenException_whenGettingActivationPath() {
                // Arrange
                String dynamicPath = mailingConfigData.getRegisterEmail().getPath();
                // Given
                String token = UUID.randomUUID().toString();
                // When
                when(authService.activeUserAccount(anyString())).thenThrow(new TokenExpiredException("Dummy Error"));
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenThrow(new TokenExpiredException("Dummy Error"));
                // Then
                mockMvc.perform(get("/auth" + dynamicPath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .param("token", token))
                                .andDo(print())
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl(fontendServerConfigData.getActive().get("expired")));
        }

        @SuppressWarnings("unchecked")
        @SneakyThrows
        @Test
        @DisplayName("Should redirect to error page after throwing an exception")
        void shouldRedirectToErrorPagesAfterAnException_whenGettingActivationPath() {
                // Arrange
                String dynamicPath = mailingConfigData.getRegisterEmail().getPath();
                // Given
                String token = UUID.randomUUID().toString();
                // When
                when(authService.activeUserAccount(anyString())).thenThrow(new RuntimeException("Dummy Error"));
                when(responseService.mapping(any(ThrowingSupplier.class), any(RateLimiterPlan.class)))
                                .thenThrow(new RuntimeException("Dummy Error"));
                // Then
                mockMvc.perform(get("/auth" + dynamicPath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .param("token", token))
                                .andDo(print())
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl(
                                                String.format(fontendServerConfigData.getUnknown(), "Dummy Error")));
        }

}
