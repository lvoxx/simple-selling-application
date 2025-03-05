package com.shitcode.demo1.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.SpringUserDTO;
import com.shitcode.demo1.dto.SpringUserDTO.AdminRequest;
import com.shitcode.demo1.dto.SpringUserDTO.Response;
import com.shitcode.demo1.dto.SpringUserDTO.UserRequest;
import com.shitcode.demo1.entity.RegistrationToken;
import com.shitcode.demo1.entity.SpringUser;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.mapper.SpringUserMapper;
import com.shitcode.demo1.repository.SpringUserRepository;
import com.shitcode.demo1.service.MailService;
import com.shitcode.demo1.service.RegistrationTokenService;
import com.shitcode.demo1.service.SpringUserService;
import com.shitcode.demo1.utils.ApplicationRoles;
import com.shitcode.demo1.utils.LogPrinter;

@Service
@Transactional
@LogCollector
public class SpringUserServiceImpl implements SpringUserService {

    private final SpringUserRepository springUserRepository;
    private final RegistrationTokenService tokenService;
    private final MailService mailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private SpringUserMapper mapper;

    public SpringUserServiceImpl(SpringUserRepository springUserRepository, RegistrationTokenService tokenService,
            MailService mailService, BCryptPasswordEncoder passwordEncoder, SpringUserMapper mapper) {
        this.springUserRepository = springUserRepository;
        this.tokenService = tokenService;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    private SpringUserMapper springUserMapper = SpringUserMapper.INSTANCE;

    @Override
    public SpringUserDTO.Response publicCreateUser(SpringUserDTO.UserRequest request) {
        SpringUser user = springUserMapper.toSpringUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLocked(true);
        user.setEnabled(true);
        user.setPoints(BigDecimal.valueOf(0));
        user.setRoles(List.of(ApplicationRoles.USER.getRole()));

        return createUser(user, true);
    }

    @Override
    public SpringUserDTO.Response privateCreateUser(SpringUserDTO.AdminRequest request) {
        SpringUser user = springUserMapper.toSpringUser(request);
        return createUser(user, request.getIsValidEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public SpringUserDTO.Response findByEmailWithDTO(String username) {
        return mapper.toSpringUserResponse(findByEmail(username));
    }

    @Override
    @Transactional(readOnly = true)
    public SpringUser findByEmailWithoutDTO(String username) {
        return findByEmail(username);
    }

    @Override
    public Response publicUpdateUser(UserRequest request, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'publicUpdateUser'");
    }

    @Override
    public Response privateUpdateUser(AdminRequest request, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'privateUpdateUser'");
    }

    @Override
    public Response lockUser(Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'lockUser'");
    }

    @Override
    public Response disableUser(Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'disableUser'");
    }

    private SpringUserDTO.Response createUser(SpringUser user, boolean isValidEmail) {
        SpringUser result = springUserRepository.saveAndFlush(user);
        // If requesting validation email
        if (isValidEmail) {
            // Flush registration token data
            RegistrationToken token = tokenService.createToken(result.getId());
            // Send activation email
            try {
                mailService.sendActivationEmail(result.getEmail(), token.getToken(),
                        String.format("%s %s", result.getFirstName(), result.getLastName()));
            } catch (Exception e) {
                LogPrinter.printServiceLog(LogPrinter.Type.ERROR, "AuthServiceImpl", "signUp",
                        LocalDateTime.now().toString(),
                        e.getMessage());
            }
        }

        return mapper.toSpringUserResponse(result);
    }

    private SpringUser findByEmail(String username) {
        return springUserRepository.findByEmail(username).orElseThrow(
                () -> new EntityExistsException(String.format("User with email %s doesn't exist", username)));
    }

}
