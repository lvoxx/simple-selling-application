package com.shitcode.demo1.annotation.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.shitcode.demo1.annotation.logging.impl.LoggingCollectorAspect;
import com.shitcode.demo1.utils.LoggingModel;

/**
 * Annotation to mark methods or classes for logging using
 * {@link LoggingCollectorAspect}.
 * It allows specifying an additional message and the logging model to
 * categorize the log.
 * 
 * @author Lvoxx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface LogCollector {
    /**
     * An optional extra message to be included in the log output.
     * 
     * @return the extra message string.
     */
    String extraMessage() default "";

    /**
     * Specifies the logging model to categorize the log (e.g., CONTROLLER,
     * SERVICE).
     * Default is {@link LoggingModel#CONTROLLER}.
     * 
     * @return the logging model.
     */
    LoggingModel loggingModel() default LoggingModel.CONTROLLER;
}