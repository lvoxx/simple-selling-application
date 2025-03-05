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
import jakarta.validation.constraints.Pattern;
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
        @NotBlank(message = "Email cannot be blank")
        @Size(min = 6, max = 255, message = "Email must be between 6 and 255 characters")
        @Email(message = "{validation.new-user.email}")
        @Schema(description = "User email address", example = "user@example.com", required = true)
        private final String email;

        @NotBlank(message = "Password cannot be blank")
        @Password(message = "{validation.new-user.password}", minSize = 6, maxSize = 30, containsSpecialChar = true)
        @Schema(description = "User password", example = "P@ssw0rd!", required = true)
        private final String password;

        @NotBlank(message = "First name cannot be blank")
        @Size(max = 60, message = "First name must not exceed 60 characters")
        @Schema(description = "User's first name", example = "Yae", required = true)
        private final String firstName;

        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 60, message = "Last name must not exceed 60 characters")
        @Schema(description = "User's last name", example = "Miiko", required = true)
        private final String lastName;

        @Phone(message = "{validation.new-user.phone}")
        @Size(min = 4, max = 14)
        @Schema(description = "User's phone number", example = "+1-5551234567", required = true)
        private final String phoneNumber;
    }

    @Builder
    @AllArgsConstructor
    @Setter(value = AccessLevel.PRIVATE)
    @Getter
    public static class AdminRequest {
        @NotBlank(message = "Email cannot be blank")
        @Size(min = 6, max = 255, message = "Email must be between 6 and 255 characters")
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character (@#$%^&+=)")
        private String password;

        @NotBlank(message = "First name cannot be blank")
        @Size(max = 60, message = "First name must not exceed 60 characters")
        private String firstName;

        @NotBlank(message = "Last name cannot be blank")
        @Size(max = 60, message = "Last name must not exceed 60 characters")
        private String lastName;

        @NotEmpty(message = "Roles are not specified")
        private List<String> roles;

        @DecimalMin(value = "0.0", inclusive = true, message = "Points must be a positive number")
        @Digits(integer = 10, fraction = 2, message = "Points must be a valid number with up to 10 digits and 2 decimal places")
        @Builder.Default
        private BigDecimal points = BigDecimal.valueOf(0l);

        @NotNull(message = "Is Locked cannot be null")
        private Boolean locked;

        @NotNull(message = "is Enabled cannot be null")
        private Boolean enabled;

        @NotNull(message = "IsValidEmail cannot be null")
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