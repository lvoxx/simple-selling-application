package com.shitcode.demo1.annotation.validation.impl;

import java.util.regex.Pattern;

import com.shitcode.demo1.annotation.validation.Email;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<Email, String> {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    public void initialize(Email constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return false; // Empty or null emails are considered invalid
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}