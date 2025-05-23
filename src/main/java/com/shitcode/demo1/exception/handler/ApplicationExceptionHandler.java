package com.shitcode.demo1.exception.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.paypal.core.rest.PayPalRESTException;
import com.shitcode.demo1.exception.model.ConflictTokenException;
import com.shitcode.demo1.exception.model.DiscountOverTimeException;
import com.shitcode.demo1.exception.model.EmptyFileException;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotChangedException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.exception.model.ErrorModel;
import com.shitcode.demo1.exception.model.ExceededRateLimterException;
import com.shitcode.demo1.exception.model.FileReadException;
import com.shitcode.demo1.exception.model.FolderNotFoundException;
import com.shitcode.demo1.exception.model.ImageEncodeException;
import com.shitcode.demo1.exception.model.InitialGoogleDriveContextException;
import com.shitcode.demo1.exception.model.KeyLockMissedException;
import com.shitcode.demo1.exception.model.OutOfStockException;
import com.shitcode.demo1.exception.model.ResourceNotFoundException;
import com.shitcode.demo1.exception.model.RevokeTokenException;
import com.shitcode.demo1.exception.model.SendingMailException;
import com.shitcode.demo1.exception.model.TokenExpiredException;
import com.shitcode.demo1.exception.model.UploadFileOnGoogleDriveException;
import com.shitcode.demo1.exception.model.VideoEncodeException;
import com.shitcode.demo1.exception.model.WorkerBusyException;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler({ KeyLockMissedException.class, WorkerBusyException.class, InvalidParameterException.class,
            SendingMailException.class, FileReadException.class, VideoEncodeException.class, ImageEncodeException.class,
            IOException.class, UploadFileOnGoogleDriveException.class, InitialGoogleDriveContextException.class,
            PayPalRESTException.class
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

    @ExceptionHandler({ ExceededRateLimterException.class })
    public ResponseEntity<ErrorModel> handleDDoSException(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
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

    @ExceptionHandler({ EntityNotFoundException.class, ResourceNotFoundException.class, FileNotFoundException.class,
            FolderNotFoundException.class
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

    @ExceptionHandler({ OutOfStockException.class })
    public ResponseEntity<ErrorModel> handleReasonaleRequestsException(RuntimeException ex) {
        ErrorModel errorResponse = ErrorModel.of(HttpStatus.NOT_ACCEPTABLE, ex.getMessage(), null);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }
}