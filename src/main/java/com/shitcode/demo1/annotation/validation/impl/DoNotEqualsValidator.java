package com.shitcode.demo1.annotation.validation.impl;

import com.shitcode.demo1.annotation.validation.DoNotEquals;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DoNotEqualsValidator implements ConstraintValidator<DoNotEquals, String> {
    private String forbiddenValue;

    @Override
    public void initialize(DoNotEquals constraintAnnotation) {
        this.forbiddenValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.equals(forbiddenValue);
    }
}
