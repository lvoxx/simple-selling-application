package com.shitcode.demo1.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.shitcode.demo1.annotation.validation.impl.LessThanValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LessThanValidator.class)
public @interface LessThan {
    String message() default "Value must be less than {value}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    double value();
}
