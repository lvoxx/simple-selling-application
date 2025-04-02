package com.shitcode.demo1.annotation.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.shitcode.demo1.annotation.validation.impl.ImageFilesValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ImageFilesValidator.class})
public @interface ValidImages {
    String message() default "Invalid image files format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
    boolean nullable() default false;
}