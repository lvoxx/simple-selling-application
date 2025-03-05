package com.shitcode.demo1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import com.shitcode.demo1.entity.RegistrationToken;
import com.shitcode.demo1.testcontainer.AbstractRepositoryTest;

@AutoConfigureTestDatabase(replace = Replace.NONE) // Dont load String datasource autoconfig
@ActiveProfiles("test")
@DisplayName("Registration Token Repository Tests")
@Tags({
        @Tag("Reporitory"), @Tag("No Mock")
})
public class RegistrationTokenRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    RegistrationTokenRepository repository;

    private final Instant expTime = Instant.now().plus((long) 15, ChronoUnit.MINUTES);
    private final String token = UUID.randomUUID().toString();
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        RegistrationToken registrationToken = RegistrationToken.builder()
                .token(token)
                .expirationTime(expTime)
                .userId(userId)
                .build();

        repository.save(registrationToken);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should return token when finding by valid token")
    void shouldReturnTokenWhenFindingByToken() {
        // When
        Optional<RegistrationToken> res = repository.findByToken(token);
        // Then
        assertThat(res.get()).isNotNull().satisfies(t -> {
            assertThat(t.getToken()).isEqualTo(token);
            assertThat(t.getExpirationTime()).isEqualTo(expTime);
            assertThat(t.getUserId()).isEqualTo(userId);
        });
    }

    @Test
    @DisplayName("Should not return token when finding by non-existent token")
    void shouldNotReturnTokenWhenFindingByNonExistanceToken() {
        // When
        Optional<RegistrationToken> res = repository.findByToken("Dummy Token");
        // Then
        assertThat(res).isNotPresent();
    }

    @Test
    @DisplayName("Should return token when finding by valid user ID")
    void shouldReturnTokenWhenFindingByUserId() {
        // When
        Optional<RegistrationToken> res = repository.findByUserId(userId);
        // Then
        assertThat(res.get()).isNotNull().satisfies(t -> {
            assertThat(t.getToken()).isEqualTo(token);
            assertThat(t.getExpirationTime()).isEqualTo(expTime);
            assertThat(t.getUserId()).isEqualTo(userId);
        });
    }

    @Test
    @DisplayName("Should not return token when finding by non-existent user ID")
    void shouldNotReturnTokenWhenFindingByNonExistanceUserId() {
        // When
        Optional<RegistrationToken> res = repository.findByUserId(-1L);
        // Then
        assertThat(res).isNotPresent();
    }

}
