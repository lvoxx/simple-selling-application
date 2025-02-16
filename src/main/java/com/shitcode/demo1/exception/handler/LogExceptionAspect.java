package com.shitcode.demo1.exception.handler;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExceptionAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogExceptionAspect.class);

    // Define a pointcut for any exception thrown by the service layer
    @AfterThrowing(pointcut = "execution(* com.shitcode.demo1.service..*(..))", throwing = "ex")
    public void handleException(RuntimeException ex) {
        // Log the exception with full details
        logger.error("Catched an error throwed from services layer: {}", ex.getMessage(), ex);
    }
}