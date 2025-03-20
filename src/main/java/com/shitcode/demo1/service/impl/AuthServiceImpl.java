package com.shitcode.demo1.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.AuthDTO;
import com.shitcode.demo1.dto.GenericDTO;
import com.shitcode.demo1.dto.SpringUserDTO;
import com.shitcode.demo1.entity.RegistrationToken;
import com.shitcode.demo1.entity.SpringUser;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.jwt.JwtService;
import com.shitcode.demo1.mapper.SpringUserMapper;
import com.shitcode.demo1.properties.ClientConfigData;
import com.shitcode.demo1.service.AuthService;
import com.shitcode.demo1.service.RegistrationTokenService;
import com.shitcode.demo1.service.SpringUserService;
import com.shitcode.demo1.utils.LoggingModel;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Implementation of the {@link AuthService} interface, responsible for
 * authentication and user management.
 * This service handles user login, registration, and account activation.
 * <p>
 * It integrates with JWT authentication, user management, and token services to
 * provide a secure authentication process.
 * </p>
 */
@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final SpringUserService springUserService;
    private final RegistrationTokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final MessageSource messageSource;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ClientConfigData clientConfigData;

    /**
     * Constructs an instance of {@code AuthServiceImpl} with required dependencies.
     *
     * @param jwtService            Service for generating and validating JWT
     *                              tokens.
     * @param springUserService     Service handling user-related operations.
     * @param tokenService          Service managing registration tokens.
     * @param authenticationManager Spring Security authentication manager.
     * @param messageSource         Message source for localization.
     * @param passwordEncoder       Encoder for hashing passwords.
     * @param clientConfigData      Configuration data for client roles.
     */
    public AuthServiceImpl(JwtService jwtService, SpringUserService springUserService,
            RegistrationTokenService tokenService, AuthenticationManager authenticationManager,
            MessageSource messageSource, BCryptPasswordEncoder passwordEncoder, ClientConfigData clientConfigData) {
        this.jwtService = jwtService;
        this.springUserService = springUserService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
        this.clientConfigData = clientConfigData;
    }

    private SpringUserMapper springUserMapper = SpringUserMapper.INSTANCE;

    /**
     * Authenticates a user and returns an access token and refresh token.
     *
     * @param request The login request containing email and password.
     * @return A response containing the access token and refresh token.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        return AuthDTO.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Registers a new user in the system.
     *
     * @param request The user registration request.
     * @return The created user's response DTO.
     * @throws EntityExistsException if the email is already registered.
     */
    @Override
    public SpringUserDTO.Response signUp(SpringUserDTO.UserRequest request) {
        Optional.ofNullable(springUserService.findByEmailWithDTO(request.getEmail())).ifPresent(u -> {
            throw new EntityExistsException("{exception.entity-exists.user}");
        });

        SpringUser user = springUserMapper.toSpringUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLocked(true);
        user.setEnabled(true);
        user.setPoints(BigDecimal.valueOf(0));
        user.setRoles(List.of(clientConfigData.getRoles().getUser()));

        return springUserService.createUser(user, true);
    }

    /**
     * Activates a user's account based on a registration token.
     *
     * @param token The activation token.
     * @return A response indicating successful activation.
     * @throws EntityNotFoundException if the token is invalid.
     */
    @Override
    public GenericDTO.Response activeUserAccount(String token) {
        RegistrationToken registrationToken = tokenService.findByToken(token);
        springUserService.lockOrNotUser(registrationToken.getUserId(), false);
        return GenericDTO.Response.builder()
                .message(messageSource.getMessage("success.user.active", null, Locale.getDefault())).build();
    }

    /**
     * Retrieves the authenticated user's username.
     *
     * @return The username of the authenticated user, or "Anonymous User" if no
     *         user is authenticated.
     */
    public static String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "Anonymous User";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return "anonymousUser".equals(principal) ? "Anonymous User" : (String) principal;
        }

        return "Anonymous User";
    }

    /**
     * Retrieves the current HTTP servlet request.
     *
     * @return The {@link HttpServletRequest} associated with the current request.
     * @throws IllegalStateException if there is no active request context.
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();
    }
}
