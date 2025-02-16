package com.shitcode.demo1.exception.handler;

import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.shitcode.demo1.exception.model.ErrorModel;
import com.shitcode.demo1.helper.DatetimeFormat;
import com.shitcode.demo1.helper.DatetimeFormat.Format;
import com.shitcode.demo1.helper.DatetimeFormat.TimeConversionMode;

@ControllerAdvice
public class GlobalExceptionHandler {

        // Handle MethodArgumentNotValidException (for validation errors)
        @ExceptionHandler({ MethodArgumentNotValidException.class, InvalidParameterException.class })
        public ResponseEntity<ErrorModel> handleValidationExceptions(MethodArgumentNotValidException ex,
                        WebRequest request) {
                List<String> errorDetails = ex.getBindingResult()
                                .getAllErrors()
                                .stream()
                                .map(objectError -> objectError.getDefaultMessage())
                                .collect(Collectors.toList());

                ErrorModel errorResponse = ErrorModel.builder()
                                .message("Validation failed")
                                .contextPath(request.getContextPath())
                                .errors(errorDetails)
                                .code(HttpStatus.BAD_REQUEST.value())
                                .time(DatetimeFormat.format(Instant.now().getEpochSecond(), TimeConversionMode.INSTANT, Format.FORMAL))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle User Account exception
        @ExceptionHandler({ DisabledException.class })
        public ResponseEntity<ErrorModel> handleValidationExceptions(Exception ex,
                        WebRequest request) {
                ErrorModel errorResponse = ErrorModel.builder()
                                .message("An error occurred")
                                .contextPath(request.getContextPath())
                                .errors(List.of(ex.getMessage()))
                                .code(HttpStatus.LOCKED.value())
                                .time(DatetimeFormat.format(Instant.now().getEpochSecond(), TimeConversionMode.INSTANT, Format.FORMAL))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.LOCKED);
        }

        // Handle other generic exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorModel> handleGenericException(Exception ex, WebRequest request) {
                ErrorModel errorResponse = ErrorModel.builder()
                                .message("An error occurred")
                                .contextPath(request.getContextPath())
                                .errors(List.of(ex.getMessage()))
                                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .time(DatetimeFormat.format(Instant.now().getEpochSecond(), TimeConversionMode.INSTANT, Format.FORMAL))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Handle BindException (for binding-related errors)
        @ExceptionHandler(BindException.class)
        public ResponseEntity<ErrorModel> handleBindException(BindException ex, WebRequest request) {
                List<String> errorDetails = ex.getBindingResult()
                                .getAllErrors()
                                .stream()
                                .map(objectError -> objectError.getDefaultMessage())
                                .collect(Collectors.toList());

                ErrorModel errorResponse = ErrorModel.builder()
                                .message("Binding error")
                                .contextPath(request.getContextPath())
                                .errors(errorDetails)
                                .code(HttpStatus.BAD_REQUEST.value())
                                .time(DatetimeFormat.format(Instant.now().getEpochSecond(), TimeConversionMode.INSTANT, Format.FORMAL))
                                .build();

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // * README: Handle other specific exceptions
        // @ExceptionHandler(YourCustomException.class)
        // public ResponseEntity<ErrorModel> handleCustomException(YourCustomException
        // ex, WebRequest request) {
        // ErrorModel errorResponse = ErrorModel.builder()
        // .message("Custom error")
        // .contextPath(request.getContextPath())
        // .errors(List.of(ex.getMessage()))
        // .time(DatetimeFormat.format(Instant.now().getEpochSecond(), TimeConversionMode.INSTANT, Format.FORMAL))
        // .build();

        // return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        // }
}