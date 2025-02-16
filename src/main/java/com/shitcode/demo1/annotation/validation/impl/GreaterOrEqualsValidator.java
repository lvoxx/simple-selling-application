package com.shitcode.demo1.annotation.validation.impl;

import com.shitcode.demo1.annotation.validation.GreaterOrEquals;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GreaterOrEqualsValidator implements ConstraintValidator<GreaterOrEquals, Number> {
    private double minValue;

    @Override
    public void initialize(GreaterOrEquals constraintAnnotation) {
        this.minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return value != null && (value.doubleValue() > minValue || value.doubleValue() == minValue);
    }
}
