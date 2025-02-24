package com.shitcode.demo1.exception.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.shitcode.demo1.exception.model.ErrorModel;

@ControllerAdvice
public class GlobalExceptionHandler {

        // Handle validation exceptions (for validation errors)
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorModel> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
                HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

                List<ErrorModel.FieldError> errors = ex.getBindingResult().getFieldErrors()
                                .stream()
                                .map(fieldError -> ErrorModel.FieldError.builder()
                                                .field(fieldError.getField())
                                                .message(fieldError.getDefaultMessage())
                                                .build())
                                .collect(Collectors.toList());

                return ResponseEntity.status(status)
                                .body(ErrorModel.of(status, "Validation failed", errors));
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorModel> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
                ErrorModel errorResponse = ErrorModel.of(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Handle User Account exception
        @ExceptionHandler({ DisabledException.class })
        public ResponseEntity<ErrorModel> handleValidationExceptions(Exception ex) {
                ErrorModel errorResponse = ErrorModel.of(HttpStatus.LOCKED, ex.getMessage(), null);
                return new ResponseEntity<>(errorResponse, HttpStatus.LOCKED);
        }

        // Handle other generic exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorModel> handleGenericException(Exception ex) {
                ErrorModel errorResponse = ErrorModel.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // * README: Handle other specific exceptions
        // @ExceptionHandler(YourCustomException.class)
        // public ResponseEntity<ErrorModel> handleCustomException(YourCustomException
        // ex, WebRequest request) {
        // ErrorModel errorResponse = ErrorModel.builder()
        // .message("Custom error")
        // .contextPath(request.getContextPath())
        // .errors(List.of(ex.getMessage()))
        // .time(DatetimeFormat.format(Instant.now().getEpochSecond(),
        // TimeConversionMode.INSTANT, Format.FORMAL))
        // .build();

        // return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        // }
}