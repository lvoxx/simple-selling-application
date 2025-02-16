package com.shitcode.demo1.annotation.validation.impl;

import com.shitcode.demo1.annotation.validation.GreaterThan;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GreaterThanValidator implements ConstraintValidator<GreaterThan, Number> {
    private double minValue;

    @Override
    public void initialize(GreaterThan constraintAnnotation) {
        this.minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return value != null && value.doubleValue() > minValue;
    }
}
