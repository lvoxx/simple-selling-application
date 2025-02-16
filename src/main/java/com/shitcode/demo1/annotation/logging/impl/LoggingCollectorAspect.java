package com.shitcode.demo1.annotation.logging.impl;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.helper.DatetimeFormat;
import com.shitcode.demo1.helper.DatetimeFormat.Format;
import com.shitcode.demo1.helper.DatetimeFormat.TimeConversionMode;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.LoggingModel;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Aspect for logging method execution details based on the {@link LogCollector}
 * annotation.
 * This aspect captures and logs relevant information before the execution of
 * methods
 * or classes annotated with {@link LogCollector}. It dynamically handles
 * different logging
 * models such as CONTROLLER, SERVICE, REPOSITORY, ASPECT, and UTILS.
 * 
 * The log message includes details like class name, method name, execution
 * time,
 * and any additional message specified in the annotation.
 * 
 * @author Lvoxx
 */
@Aspect
@Component
public class LoggingCollectorAspect {

    /**
     * Pointcut to match method executions where the method or its declaring class
     * is annotated with {@link LogCollector}.
     * 
     * @param joinPoint the join point providing reflective access to the method
     *                  being executed.
     */
    @Before("execution(* *(..)) && (@annotation(com.shitcode.demo1.annotation.logging.LogCollector) || " +
            "@within(com.shitcode.demo1.annotation.logging.LogCollector))")
    public void logBefore(JoinPoint joinPoint) {
        // * Get annotation configuration
        LogCollector annotation = extractServiceLoggingCollector(joinPoint);

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String time = DatetimeFormat.format(Instant.now().getEpochSecond(), TimeConversionMode.INSTANT, Format.FORMAL);

        String extraMessage = annotation.extraMessage();
        LoggingModel model = annotation.loggingModel();

        // Define logging behavior based on the LoggingModel
        Map<LoggingModel, Consumer<Void>> logResult = Map.<LoggingModel, Consumer<Void>>ofEntries(
                Map.entry(LoggingModel.SERVICE,
                        l -> LogPrinter.printServiceLog(LogPrinter.Type.INFO, className, methodName, time,
                                extraMessage)),
                Map.entry(LoggingModel.REPOSITORY,
                        l -> LogPrinter.printRepositoryLog(LogPrinter.Type.INFO, className, methodName, time,
                                extraMessage)),
                Map.entry(LoggingModel.ASPECT,
                        l -> LogPrinter.printAspectLog(LogPrinter.Type.INFO, className, methodName, time,
                                extraMessage)),
                Map.entry(LoggingModel.UTILS,
                        l -> LogPrinter.printUtilsLog(LogPrinter.Type.INFO, className, methodName, time,
                                extraMessage)),
                Map.entry(LoggingModel.CONTROLLER, l -> {
                    HttpServletRequest request = getCurrentHttpRequest();
                    LogPrinter.printControllerLog(LogPrinter.Type.INFO, request.getRequestURI(),
                            className, methodName, time,
                            extraMessage);
                }));

        logResult.get(model).accept(null);
    }

    /**
     * Extracts the {@link LogCollector} annotation from the method or class level.
     * 
     * @param joinPoint the join point providing access to method details.
     * @return the {@link LogCollector} annotation if present, otherwise null.
     */
    private LogCollector extractServiceLoggingCollector(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // * First, attempt to retrieve the annotation from the method.
        LogCollector annotation = method.getAnnotation(LogCollector.class);
        if (annotation == null) {
            // * Fallback to the class-level annotation.
            annotation = joinPoint.getTarget().getClass().getAnnotation(LogCollector.class);
        }
        return annotation;
    }

    /**
     * Retrieves the current {@link HttpServletRequest} dynamically.
     * 
     * @return the current HTTP request if in a web context, otherwise null.
     */
    private HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null; // Return null if not in a web context
    }
}
