package com.shitcode.demo1.annotation.validation.impl;

import com.shitcode.demo1.annotation.validation.DoEquals;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DoEqualsValidator implements ConstraintValidator<DoEquals, String> {
    private String expectedValue;

    @Override
    public void initialize(DoEquals constraintAnnotation) {
        this.expectedValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.equals(expectedValue);
    }
}
