package com.shitcode.demo1.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.AuthDTO;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.dto.SpringUserDTO;
import com.shitcode.demo1.exception.model.ErrorModel;
import com.shitcode.demo1.exception.model.TokenExpiredException;
import com.shitcode.demo1.exception.model.UserDisabledException;
import com.shitcode.demo1.properties.FontendServerConfigData;
import com.shitcode.demo1.properties.RateLimiterConfigData;
import com.shitcode.demo1.service.AuthService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.service.SpringUserService;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LogPrinter.Type;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user authentication")
@LogCollector
public class AuthController {

        @Value("#{ @mailingConfigData.getRegisterEmail().getPath() }")
        private String registerEmailPath;

        private final AuthService authService;
        private final SpringUserService springUserService;
        private final ResponseService responseService;
        private final RateLimiterConfigData rateLimiterConfigData;
        private final FontendServerConfigData fontendServerConfigData;

        public AuthController(AuthService authService, SpringUserService springUserService,
                        ResponseService responseService, RateLimiterConfigData rateLimiterConfigData,
                        FontendServerConfigData fontendServerConfigData) {
                this.authService = authService;
                this.springUserService = springUserService;
                this.responseService = responseService;
                this.rateLimiterConfigData = rateLimiterConfigData;
                this.fontendServerConfigData = fontendServerConfigData;
        }

        private RateLimiterPlan LOGIN_PLAN;
        private RateLimiterPlan SIGNUP_PLAN;
        private RateLimiterPlan COMPLETE_SIGNUP;

        @PostConstruct
        public void setup() {
                LOGIN_PLAN = rateLimiterConfigData.getRateLimiterPlan("auth", "login");
                SIGNUP_PLAN = rateLimiterConfigData.getRateLimiterPlan("auth", "signup");
                COMPLETE_SIGNUP = rateLimiterConfigData.getRateLimiterPlan("auth", "complete_signup");
        }

        @Operation(summary = "User login", description = "Authenticate a user and return JWT tokens.", tags = {
                        "Authentication" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "429", description = "Too many request", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class)))
        })
        @PostMapping(path = "/login", produces = "application/vnd.lvoxx.app-v1+json")
        public ResponseEntity<?> loginV1(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login credentials", required = true, content = @Content(schema = @Schema(implementation = AuthDTO.LoginRequest.class))) @Valid @RequestBody AuthDTO.LoginRequest request)
                        throws Exception {
                return responseService.mapping(
                                () -> new ResponseEntity<>(authService.login(request), HttpStatus.OK),
                                LOGIN_PLAN);
        }

        @Operation(summary = "User signup", description = "User signup and send a registration email.", tags = {
                        "Authentication" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "409", description = "Registration token conflicts", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "418", description = "Registration token already inuse", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "429", description = "Too many request", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class)))
        })
        @PostMapping(path = "/signup", produces = "application/vnd.lvoxx.app-v1+json")
        public ResponseEntity<?> signUpV1(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login credentials", required = true, content = @Content(schema = @Schema(implementation = SpringUserDTO.UserRequest.class))) @Valid @RequestBody SpringUserDTO.UserRequest request)
                        throws Exception {
                return responseService.mapping(
                                () -> new ResponseEntity<>(authService.signUp(request),
                                                HttpStatus.CREATED),
                                SIGNUP_PLAN);
        }

        // /auth/activate?token=...
        @Operation(summary = "Active user account", description = "Active user account that registered", tags = {
                        "Authentication" })
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Active user successfully", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Registration token not found", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "410", description = "Registration token is expired", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "422", description = "User is locked", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class))),
                        @ApiResponse(responseCode = "429", description = "Too many request", content = @Content(mediaType = "application/vnd.lvoxx.app-v1+json", schema = @Schema(implementation = ErrorModel.class)))
        })
        @GetMapping(path = "#{@mailingConfigData.getRegisterEmail().getPath()}", produces = "application/vnd.lvoxx.app-v1+json")
        public void activeUserV1(@RequestParam String token, HttpServletRequest request, HttpServletResponse response)
                        throws Exception {
                try {
                        responseService.mapping(
                                        () -> new ResponseEntity<>(authService.activeUserAccount(token),
                                                        HttpStatus.OK),
                                        COMPLETE_SIGNUP);
                } catch (UserDisabledException | TokenExpiredException e) {
                        if (e instanceof UserDisabledException) {
                                response.sendRedirect(fontendServerConfigData.getActive().get("disabled"));
                        } else if (e instanceof TokenExpiredException) {
                                response.sendRedirect(fontendServerConfigData.getActive().get("expired"));
                        }
                } catch (Exception e) {
                        LogPrinter.printControllerLog(Type.ERROR, request.getContextPath(), "AuthController",
                                        "activeUserV1", LocalDateTime.now().toString(), e.getMessage());
                        response.sendRedirect(String.format(fontendServerConfigData.getUnknown(), e.getMessage()));
                }
                response.sendRedirect(fontendServerConfigData.getActive().get("success"));
        }

}
