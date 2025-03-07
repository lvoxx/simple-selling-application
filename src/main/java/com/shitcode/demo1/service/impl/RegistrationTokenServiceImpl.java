package com.shitcode.demo1.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.entity.RegistrationToken;
import com.shitcode.demo1.exception.model.ConflictTokenException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.exception.model.RevokeTokenException;
import com.shitcode.demo1.exception.model.TokenExpiredException;
import com.shitcode.demo1.helper.DateFormatConverter;
import com.shitcode.demo1.properties.AuthTokenConfigData;
import com.shitcode.demo1.repository.RegistrationTokenRepository;
import com.shitcode.demo1.service.RegistrationTokenService;
import com.shitcode.demo1.utils.LoggingModel;

@Service
@Transactional
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class RegistrationTokenServiceImpl implements RegistrationTokenService {

    private final RegistrationTokenRepository repository;
    private final AuthTokenConfigData authTokenConfigData;

    public RegistrationTokenServiceImpl(RegistrationTokenRepository repository,
            AuthTokenConfigData authTokenConfigData) {
        this.repository = repository;
        this.authTokenConfigData = authTokenConfigData;
    }

    @Override
    public RegistrationToken createToken(Long userId) {
        Optional.ofNullable(repository.findByUserId(userId).get()).ifPresent(t -> {
            // Link is expried, revoke token
            if (t.getExpirationTime().compareTo(Instant.now()) >= 0) {
                revokeToken(userId);
                throw new RevokeTokenException("{exception.registration.revoke}");
            }
            // Signup email is on date, don't resend
            throw new ConflictTokenException("{exception.registration.conflict}");
        });
        String token = UUID.randomUUID().toString();
        Instant expirationTime = DateFormatConverter.generateExpirationDate(authTokenConfigData.getRegisterExpDate(),
                authTokenConfigData.getRegisterExpDateFormat());

        RegistrationToken registrationToken = RegistrationToken.builder()
                .token(token)
                .expirationTime(expirationTime)
                .userId(userId)
                .build();
        return repository.save(registrationToken);
    }

    @Override
    public RegistrationToken revokeToken(Long userId) {
        RegistrationToken registrationToken = findByUserId(userId);

        String token = UUID.randomUUID().toString();
        Instant expirationTime = DateFormatConverter.generateExpirationDate(authTokenConfigData.getRegisterExpDate(),
                authTokenConfigData.getRegisterExpDateFormat());
        registrationToken.setExpirationTime(expirationTime);
        registrationToken.setToken(token);

        return repository.save(registrationToken);
    }

    private RegistrationToken findByUserId(Long userId) {
        RegistrationToken registrationToken = repository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("{exception.entity-not-found.registration-id}"));
        return registrationToken;
    }

    @Override
    public void deleteToken(String token) {
        repository.deleteByToken(token);
    }

    @Override
    public RegistrationToken findByToken(String token) {
        RegistrationToken registrationToken = repository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("{exception.entity-not-found.registration-token}"));
        return registrationToken;
    }

    @Override
    public RegistrationToken validToken(String token) {
        RegistrationToken registrationToken = findByToken(token);
        if (registrationToken.getExpirationTime().compareTo(Instant.now()) >= 0) {
            throw new TokenExpiredException("{exception.registration.expired}");
        }
        return registrationToken;
    }

}
