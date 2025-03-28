package com.shitcode.demo1.annotation.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.shitcode.demo1.annotation.validation.impl.PasswordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface Password {

    boolean containsUpperCase() default true;

    boolean containsLowerCase() default true;

    boolean containsSpecialChar() default true;

    boolean containsDigits() default true;

    boolean allowSpace() default false;

    int minSize() default 6;

    int maxSize() default 32;

    String message() default "Invalid password format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}