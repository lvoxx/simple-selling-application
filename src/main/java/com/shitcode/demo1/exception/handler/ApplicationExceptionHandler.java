package com.shitcode.demo1.exception.handler;

import java.io.FileNotFoundException;
import java.security.InvalidParameterException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shitcode.demo1.exception.model.ConflictTokenException;
import com.shitcode.demo1.exception.model.DiscountOverTimeException;
import com.shitcode.demo1.exception.model.EmptyFileException;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotChangedException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.exception.model.ErrorModel;
import com.shitcode.demo1.exception.model.FileReadException;
import com.shitcode.demo1.exception.model.KeyLockMissedException;
import com.shitcode.demo1.exception.model.ResourceNotFoundException;
import com.shitcode.demo1.exception.model.RevokeTokenException;
import com.shitcode.demo1.exception.model.SendingMailException;
import com.shitcode.demo1.exception.model.TokenExpiredException;
import com.shitcode.demo1.exception.model.VideoEncodeException;
import com.shitcode.demo1.exception.model.WorkerBusyException;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({ KeyLockMissedException.class, WorkerBusyException.class, InvalidParameterException.class,
            SendingMailException.class, FileReadException.class, VideoEncodeException.class
    })
    public ResponseEntity<ErrorModel> handleDataOperationException(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ EntityNotChangedException.class, DiscountOverTimeException.class })
    public ResponseEntity<ErrorModel> handleUnchangedDataOperationException(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler({ RevokeTokenException.class })
    public ResponseEntity<ErrorModel> handleOtherOffer(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.I_AM_A_TEAPOT, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler({ TokenExpiredException.class })
    public ResponseEntity<ErrorModel> handleExpire(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.GONE, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);
    }

    @ExceptionHandler({ EntityNotFoundException.class, ResourceNotFoundException.class, FileNotFoundException.class
    })
    public ResponseEntity<ErrorModel> handleDataNotFoundOperationException(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ EntityExistsException.class, ConflictTokenException.class })
    public ResponseEntity<ErrorModel> handleDataExistsOperationException(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.CONFLICT, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ EmptyFileException.class })
    public ResponseEntity<ErrorModel> handleBadRequestsException(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}