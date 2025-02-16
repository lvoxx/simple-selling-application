package com.shitcode.demo1.dto;

import java.math.BigDecimal;
import java.util.List;

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
    public static class UserRequest {
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

        @Builder.Default
        private boolean locked = true;

        @Builder.Default
        private boolean enabled = true;
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
    public static class Response extends AbstractAuditableEntity{
        private Long id;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private List<String> roles;
        private BigDecimal points;
        private boolean locked;
        private boolean enabled;
    }
}