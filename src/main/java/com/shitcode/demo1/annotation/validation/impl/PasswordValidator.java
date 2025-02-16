package com.shitcode.demo1.annotation.validation.impl;

import java.util.regex.Pattern;

import com.shitcode.demo1.annotation.validation.Password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private boolean containsUpperCase;
    private boolean containsLowerCase;
    private boolean containsSpecialChar;
    private boolean containsDigits;
    private boolean allowSpace;
    private int minSize;
    private int maxSize;

    @Override
    public void initialize(Password password) {
        this.containsUpperCase = password.containsUpperCase();
        this.containsLowerCase = password.containsLowerCase();
        this.containsSpecialChar = password.containsSpecialChar();
        this.containsDigits = password.containsDigits();
        this.allowSpace = password.allowSpace();
        this.minSize = password.minSize();
        this.maxSize = password.maxSize();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            setValidationMessage(context, "Password cannot be null.");
            return false;
        }

        if (password.length() < minSize || password.length() > maxSize) {
            setValidationMessage(context,
                    String.format("Password length must be between %d and %d characters.", minSize, maxSize));
            return false;
        }

        if (containsUpperCase && !Pattern.compile("[A-Z]").matcher(password).find()) {
            setValidationMessage(context, "Password must contain at least one uppercase letter.");
            return false;
        }

        if (containsLowerCase && !Pattern.compile("[a-z]").matcher(password).find()) {
            setValidationMessage(context, "Password must contain at least one lowercase letter.");
            return false;
        }

        if (containsDigits && !Pattern.compile("\\d").matcher(password).find()) {
            setValidationMessage(context, "Password must contain at least one digit.");
            return false;
        }

        if (containsSpecialChar && !Pattern.compile("[^A-Za-z0-9 ]").matcher(password).find()) {
            setValidationMessage(context, "Password must contain at least one special character.");
            return false;
        }

        if (!allowSpace && password.contains(" ")) {
            setValidationMessage(context, "Password must not contain spaces.");
            return false;
        }

        return true;
    }

    private void setValidationMessage(ConstraintValidatorContext context, String errorMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
    }
}