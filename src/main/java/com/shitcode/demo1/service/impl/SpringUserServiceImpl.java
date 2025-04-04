package com.shitcode.demo1.service.impl;

import java.time.LocalDateTime;

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
import com.shitcode.demo1.exception.model.EntityNotChangedException;
import com.shitcode.demo1.exception.model.SendingMailException;
import com.shitcode.demo1.exception.model.UserDisabledException;
import com.shitcode.demo1.mapper.SpringUserMapper;
import com.shitcode.demo1.repository.SpringUserRepository;
import com.shitcode.demo1.service.MailService;
import com.shitcode.demo1.service.RegistrationTokenService;
import com.shitcode.demo1.service.SpringUserService;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LoggingModel;

@Service
@Transactional
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class SpringUserServiceImpl implements SpringUserService {

    private final SpringUserRepository springUserRepository;
    private final RegistrationTokenService tokenService;
    private final MailService mailService;

    public SpringUserServiceImpl(SpringUserRepository springUserRepository, RegistrationTokenService tokenService,
            MailService mailService) {
        this.springUserRepository = springUserRepository;
        this.tokenService = tokenService;
        this.mailService = mailService;
    }

    private SpringUserMapper mapper = SpringUserMapper.INSTANCE;

    @Override
    public SpringUserDTO.Response createUser(SpringUser request, boolean isValidEmail) {
        SpringUser result = springUserRepository.saveAndFlush(request);
        // If requesting validation email
        if (isValidEmail) {
            // Flush registration token data
            RegistrationToken token = tokenService.createToken(result.getId());
            // Send activation email
            try {
                mailService.sendActivationEmail(result.getEmail(), token.getToken());
            } catch (Exception e) {
                LogPrinter.printServiceLog(LogPrinter.Type.ERROR, "AuthServiceImpl", "signUp",
                        LocalDateTime.now().toString(),
                        e.getMessage());
                throw new SendingMailException(e.getMessage(), e.getCause());
            }
        }

        return mapper.toSpringUserResponse(result);
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
    public Response lockOrNotUser(Long userId, boolean isLocked) {
        SpringUser user = findById(userId);
        return setLockOrNot(isLocked, user);
    }

    @Override
    public Response lockOrNotUser(String email, boolean isLocked) {
        SpringUser user = findByEmail(email);
        return setLockOrNot(isLocked, user);
    }

    private Response setLockOrNot(boolean isLocked, SpringUser user) {
        if (!user.isEnabled()) {
            throw new UserDisabledException(
                    String.format("{exception.account.disabled}", user.getFirstName() + " " + user.getLastName()));
        }
        if (user.isLocked() == isLocked) {
            if (user.isLocked()) {
                throw new EntityNotChangedException("{exception.entity-not-changed.user.locked}");
            }
            throw new EntityNotChangedException("{exception.entity-not-changed.user.non-locked}");
        }
        user.setLocked(isLocked);
        SpringUser res = springUserRepository.saveAndFlush(user);
        return mapper.toSpringUserResponse(res);
    }

    @Override
    public Response disableOrNotUser(Long userId, boolean isDisabled) {
        SpringUser user = findById(userId);
        return setEnabledOrNot(isDisabled, user);
    }

    @Override
    public Response disableOrNotUser(String email, boolean isDisabled) {
        SpringUser user = findByEmail(email);
        return setEnabledOrNot(isDisabled, user);
    }

    private Response setEnabledOrNot(boolean isDisabled, SpringUser user) {
        isDisabled = !isDisabled;
        if (user.isEnabled() == isDisabled) {
            if (user.isLocked()) {
                throw new EntityNotChangedException("{exception.entity-not-changed.user.enabled}");
            }
            throw new EntityNotChangedException("{exception.entity-not-changed.user.non-enabled}");
        }
        user.setEnabled(isDisabled);
        SpringUser res = springUserRepository.saveAndFlush(user);
        return mapper.toSpringUserResponse(res);
    }

    private SpringUser findByEmail(String email) {
        return springUserRepository.findByEmail(email).orElseThrow(
                () -> new EntityExistsException(String.format("{exception.entity-not-found.user-email}", email)));
    }

    private SpringUser findById(Long id) {
        return springUserRepository.findById(id).orElseThrow(
                () -> new EntityExistsException(String.format("{exception.entity-not-found.user-id}", id)));
    }

}
