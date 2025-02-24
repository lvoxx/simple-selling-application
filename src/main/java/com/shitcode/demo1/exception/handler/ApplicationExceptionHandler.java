package com.shitcode.demo1.exception.handler;

import java.security.InvalidParameterException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.exception.model.ErrorModel;
import com.shitcode.demo1.exception.model.KeyLockMissedException;
import com.shitcode.demo1.exception.model.ResourceNotFoundException;
import com.shitcode.demo1.exception.model.WorkerBusyException;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({ EntityExistsException.class, EntityNotFoundException.class, ResourceNotFoundException.class,
            KeyLockMissedException.class, WorkerBusyException.class, InvalidParameterException.class })
    public ResponseEntity<ErrorModel> handleDataOperationException(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}