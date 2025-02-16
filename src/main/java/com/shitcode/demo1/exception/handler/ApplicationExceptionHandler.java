package com.shitcode.demo1.exception.handler;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.exception.model.ErrorModel;
import com.shitcode.demo1.exception.model.KeyLockMissedException;
import com.shitcode.demo1.exception.model.ResourceNotFoundException;
import com.shitcode.demo1.exception.model.WorkerBusyException;
import com.shitcode.demo1.helper.DatetimeFormat;
import com.shitcode.demo1.helper.DatetimeFormat.Format;
import com.shitcode.demo1.helper.DatetimeFormat.TimeConversionMode;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({ EntityExistsException.class, EntityNotFoundException.class, ResourceNotFoundException.class,
            KeyLockMissedException.class, WorkerBusyException.class })
    public ResponseEntity<ErrorModel> handleDataOperationException(RuntimeException ex, WebRequest request) {
        ErrorModel errorResponse = ErrorModel.builder()
                .message(ex.getMessage())
                .contextPath(request.getContextPath())
                .errors(List.of(ex.getMessage()))
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .time(DatetimeFormat.format(Instant.now().getEpochSecond(), TimeConversionMode.INSTANT, Format.FORMAL))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}