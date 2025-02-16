package com.shitcode.demo1.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shitcode.demo1.entity.RegistrationToken;

public interface RegistrationTokenRepository extends JpaRepository<RegistrationToken, Long> {
    @Query("SELECT t FROM RegistrationToken t WHERE t.token = :token")
    Optional<RegistrationToken> findByToken(@Param("token") String token);

    @Query("SELECT t FROM RegistrationToken t WHERE t.userId = :userId")
    Optional<RegistrationToken> findByUserId(@Param("userId") Long userId);

    void deleteByToken(String token);
}