package com.shitcode.demo1.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.shitcode.demo1.annotation.validation.impl.GreaterOrEqualsValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GreaterOrEqualsValidator.class)
public @interface GreaterOrEquals {
    String message() default "Value must be greater than or equal to {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    double value();
}
