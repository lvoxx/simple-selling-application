package com.shitcode.demo1.entity;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "registration_token", // Lowercase and underscore for PostgreSQL
       indexes = {
           @Index(name = "idx_registration_token_token", columnList = "token"),
           @Index(name = "idx_registration_token_user_id", columnList = "user_id")
       })
public class RegistrationToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "user_id", nullable = false, unique = true) // Remove `unique = true` if a user can have multiple tokens
    private Long userId;

    @Column(name = "expiration_time", nullable = false)
    private Instant expirationTime;
}
