package com.shitcode.demo1.annotation.validation.impl;

import com.shitcode.demo1.annotation.validation.Phone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    private static final String PHONE_PATTERN = "\\+\\d{1,3}-\\d{4,14}";

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        return phone != null && phone.matches(PHONE_PATTERN);
    }
}