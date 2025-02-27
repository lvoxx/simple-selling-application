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
import jakarta.persistence.PrePersist;
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
@Table(name = "spring_user", // Lowercase for PostgreSQL
        indexes = @Index(name = "idx_spring_user_email", columnList = "email"))
public class SpringUser extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email; // Validation should be handled in the service layer

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", length = 60, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 60, nullable = false)
    private String lastName;

    @Column(name = "locked", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean locked = false;

    @Column(name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "points", nullable = false, columnDefinition = "NUMERIC(12,0) DEFAULT 0")
    @Builder.Default
    private BigDecimal points = BigDecimal.ZERO;

    @Column(name = "roles", columnDefinition = "TEXT")
    @Convert(converter = RoleConverter.class)
    @Builder.Default
    private List<String> roles = List.of("CUSTOMER");

    @PrePersist
    public void setDefaults() {
        if (points == null)
            points = BigDecimal.ZERO;
        if (roles == null || roles.isEmpty())
            roles = List.of("CUSTOMER");
    }
}
