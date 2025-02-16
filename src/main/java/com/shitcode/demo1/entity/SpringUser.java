package com.shitcode.demo1.entity;

import java.math.BigDecimal;
import java.util.List;

import com.shitcode.demo1.helper.RoleConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "SPRING_USER", // Uppercase table name for H2
        indexes = @Index(name = "IDX_CUSTOMER_EMAIL", columnList = "EMAIL"))
public class SpringUser extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EMAIL", unique = true, nullable = false, length = 255)
    private String email; // Remove CHECK constraint, validate in the app layer

    @Column(name = "PASSWORD", columnDefinition = "TEXT", nullable = false)
    private String password;

    @Column(name = "FIRST_NAME", length = 60, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 60, nullable = false)
    private String lastName;

    @Column(name = "LOCKED")
    @Builder.Default
    private boolean locked = false;

    @Column(name = "ENABLED")
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "POINTS", nullable = false, columnDefinition = "DECIMAL(12, 0) DEFAULT 0")
    @Builder.Default
    private BigDecimal points = BigDecimal.ZERO; // Default value at the entity level

    @Column(name = "ROLES", columnDefinition = "TEXT")
    @Convert(converter = RoleConverter.class)
    @Builder.Default
    private List<String> roles = List.of("CUSTOMER");
}