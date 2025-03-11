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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
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
import com.shitcode.demo1.config.MessagesConfig;
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
@ImportAutoConfiguration(classes = { MessagesConfig.class })
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

        @SneakyThrows
        @Test
        @DisplayName("Should return Bad Request when login request has an invalid email field")
        void shouldReturnBadRequest_whenRequestingLoginWithInvalidRequestBody_onFieldEmail() {
                // Given
                var reqFormatEmail = AuthDTO.LoginRequest.builder().email("eheeheehe").password("Abc123@#").build();
                var reqSizeEmail = AuthDTO.LoginRequest.builder().email(IntStream.range(0, 321)
                                .mapToObj(i -> "A")
                                .collect(Collectors.joining())).password("Abc123@#").build();
                // When
                // Then
                // -- Invalid Email Format --
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(reqFormatEmail)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.errors[0].field").value("email"))
                                .andExpect(jsonPath("$.errors[0].message").value("Invalid email format."))
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
                // -- Invalid Email Size --
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(reqSizeEmail)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.errors[0].field").value("email"))
                                .andExpect(jsonPath("$.errors[0].message")
                                                .value("Email must not between 6 and 320 characters, include @."))
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return Bad Request when login request has an invalid password field")
        void shouldReturnBadRequest_whenRequestingLoginWithInvalidRequestBody_onPasswordFields() {
                // Given
                var reqPasswordFormatUppercase = AuthDTO.LoginRequest.builder().email("example@mail.com")
                                .password("abcxyz")
                                .build();
                var reqPasswordFormatLowercase = AuthDTO.LoginRequest.builder().email("example@mail.com")
                                .password("ABCXYZ")
                                .build();
                var reqPasswordFormatDigits = AuthDTO.LoginRequest.builder().email("example@mail.com")
                                .password("Abcxyz")
                                .build();
                var reqPasswordFormatMinimumSize = AuthDTO.LoginRequest.builder().email("example@mail.com")
                                .password("Abc0@")
                                .build();
                var reqPasswordFormatMaximumSize = AuthDTO.LoginRequest.builder().email("example@mail.com")
                                .password("Abc0@".concat(IntStream.range(0, 60).mapToObj(c -> "a")
                                                .collect(Collectors.joining())))
                                .build();
                // When
                // Then
                // -- Invalid Password Format Uppercase --
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(reqPasswordFormatUppercase)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.errors[0].field").value("password"))
                                .andExpect(jsonPath("$.errors[0].message")
                                                .value("Password must contain at least one uppercase letter."))
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
                // -- Invalid Password Format Lowercase --
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(reqPasswordFormatLowercase)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.errors[0].field").value("password"))
                                .andExpect(jsonPath("$.errors[0].message")
                                                .value("Password must contain at least one lowercase letter."))
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
                // -- Invalid Password Format Digits --
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(reqPasswordFormatDigits)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.errors[0].field").value("password"))
                                .andExpect(jsonPath("$.errors[0].message")
                                                .value("Password must contain at least one digit."))
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
                // -- Invalid Password Format Minimum Size --
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(reqPasswordFormatMinimumSize)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.errors[0].field").value("password"))
                                .andExpect(jsonPath("$.errors[0].message")
                                                .value("Password length must be between 6 and 30 characters."))
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
                // -- Invalid Password Format Maximum Size --
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("accept", "application/vnd.lvoxx.app-v1+json")
                                .content(objectMapper.writeValueAsString(reqPasswordFormatMaximumSize)))
                                .andDo(print())
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType("application/vnd.lvoxx.app-v1+json"))
                                .andExpect(jsonPath("$.errors[0].field").value("password"))
                                .andExpect(jsonPath("$.errors[0].message")
                                                .value("Password length must be between 6 and 30 characters."))
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
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
