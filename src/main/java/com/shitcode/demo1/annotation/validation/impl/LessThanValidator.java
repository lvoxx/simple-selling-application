package com.shitcode.demo1.annotation.validation.impl;

import com.shitcode.demo1.annotation.validation.LessThan;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LessThanValidator implements ConstraintValidator<LessThan, Number> {
    private double maxValue;

    @Override
    public void initialize(LessThan constraintAnnotation) {
        this.maxValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return value != null && value.doubleValue() < maxValue;
    }
}
