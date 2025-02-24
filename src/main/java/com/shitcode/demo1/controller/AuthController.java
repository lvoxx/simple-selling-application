package com.shitcode.demo1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.AuthDTO;
import com.shitcode.demo1.service.AuthService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;

@Data
@RestController
@RequestMapping(path = "/auth", produces = "application/vnd.lvoxx.app-v1+json")
@Tag(name = "Authentication", description = "APIs for user authentication")
@LogCollector
public class AuthController {

    private final AuthService authService;
    private final ResponseService responseService;

    public AuthController(AuthService authService, ResponseService responseService) {
        this.authService = authService;
        this.responseService = responseService;
    }

    @Operation(summary = "User login", description = "Authenticate a user and return JWT tokens.", tags = {
            "Authentication" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthDTO.Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginV1(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Login credentials", required = true, content = @Content(schema = @Schema(implementation = AuthDTO.Request.class))) @Valid @RequestBody AuthDTO.Request request) {
        return responseService.mapping(() -> new ResponseEntity<>(authService.login(request), HttpStatus.CREATED),
                RateLimiterPlan.HARD);
    }

}
