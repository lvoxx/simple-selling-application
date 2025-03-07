package com.shitcode.demo1.dto;

import java.math.BigDecimal;
import java.util.List;

import com.shitcode.demo1.annotation.validation.Email;
import com.shitcode.demo1.annotation.validation.Password;
import com.shitcode.demo1.annotation.validation.Phone;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public abstract class SpringUserDTO {
    @Builder
    @AllArgsConstructor
    @Setter(value = AccessLevel.PRIVATE)
    @Getter
    @Schema(name = "SpringUserDTORequest", description = "Request payload for user signup")
    public static class UserRequest {
        @NotBlank(message = "{validation.spring-user.email.blank}")
        @Size(min = 6, max = 255, message = "{validation.spring-user.email.size}")
        @Email(message = "{validation.spring-user.email.email}")
        @Schema(description = "User email address", example = "user@example.com", required = true)
        private final String email;

        @NotBlank(message = "{validation.spring-user.password.blank}")
        @Password(message = "{validation.spring-user.password.password}", minSize = 6, maxSize = 30, containsSpecialChar = true)
        @Schema(description = "User password", example = "P@ssw0rd!", required = true)
        private final String password;

        @NotBlank(message = "{validation.spring-user.first-name.blank}")
        @Size(max = 60, message = "{validation.spring-user.first-name.size}")
        @Schema(description = "User's first name", example = "Yae", required = true)
        private final String firstName;

        @NotBlank(message = "{validation.spring-user.last-name.blank}")
        @Size(max = 60, message = "{validation.spring-user.last-name.size}")
        @Schema(description = "User's last name", example = "Miiko", required = true)
        private final String lastName;

        @Phone(message = "{validation.spring-user.phone-number.phone}")
        @Size(min = 4, max = 14,message = "{validation.spring-user.phone-number.size}")
        @Schema(description = "User's phone number", example = "+1-5551234567", required = true)
        private final String phoneNumber;
    }

    @Builder
    @AllArgsConstructor
    @Setter(value = AccessLevel.PRIVATE)
    @Getter
    public static class AdminRequest {
        @NotBlank(message = "{validation.spring-user.email.blank}")
        @Size(min = 6, max = 255, message = "{validation.spring-user.email.size}")
        @Email(message = "{validation.spring-user.email.email}")
        @Schema(description = "User email address", example = "user@example.com", required = true)
        private String email;

        @NotBlank(message = "{validation.spring-user.password.blank}")
        @Password(message = "{validation.spring-user.password.password}", minSize = 6, maxSize = 30, containsSpecialChar = true)
        @Schema(description = "User password", example = "P@ssw0rd!", required = true)
        private String password;

        @NotBlank(message = "{validation.spring-user.first-name.password.blank}")
        @Size(max = 60, message = "{validation.spring-user.first-name.password.size}")
        @Schema(description = "User's first name", example = "Yae", required = true)
        private String firstName;

        @NotBlank(message = "{validation.spring-user.last-name.blank}")
        @Size(max = 60, message = "{validation.spring-user.last-name.size}")
        @Schema(description = "User's last name", example = "Miiko", required = true)
        private String lastName;

        @Phone(message = "{validation.spring-user.phone-number.phone}")
        @Size(min = 4, max = 14,message = "{validation.spring-user.phone-number.size}")
        @Schema(description = "User's phone number", example = "+1-5551234567", required = true)
        private final String phoneNumber;

        @NotEmpty(message = "{validation.spring-user.roles.empty}")
        private List<String> roles;

        @DecimalMin(value = "0.0", inclusive = true, message = "{validation.spring-user.points.min}")
        @Digits(integer = 10, fraction = 2, message = "{validation.spring-user.points.digits}")
        @Builder.Default
        private BigDecimal points = BigDecimal.valueOf(0l);

        @NotNull(message = "{validation.spring-user.locked.null}")
        private Boolean locked;

        @NotNull(message = "{validation.spring-user.enabled.null}")
        private Boolean enabled;

        @NotNull(message = "{validation.spring-user.is-valid-email.null}")
        private Boolean isValidEmail;
    }

    @Builder
    @AllArgsConstructor
    @Setter(value = AccessLevel.PRIVATE)
    @Getter
    @Schema(name = "SpringUserDTOResponse", description = "Response containing user details and roles")
    public static class Response extends AbstractAuditableEntity {
        @Schema(description = "User ID", example = "1001")
        private Long id;

        @Schema(description = "User email address", example = "user@example.com")
        private String email;

        @Schema(description = "Encrypted user password")
        private String password;

        @Schema(description = "User's first name", example = "John")
        private String firstName;

        @Schema(description = "User's last name", example = "Doe")
        private String lastName;

        @Schema(description = "User's phone number", example = "+1-5551234567")
        private String phoneNumber;

        @Schema(description = "Indicates if the user account is locked", example = "false")
        @Builder.Default
        private boolean locked = false;

        @Schema(description = "Indicates if the user account is enabled", example = "true")
        @Builder.Default
        private boolean enabled = true;

        @Schema(description = "User's reward points", example = "100.50")
        @Builder.Default
        private BigDecimal points = BigDecimal.ZERO;

        @Schema(description = "List of assigned roles", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
        private List<String> roles;
    }
}