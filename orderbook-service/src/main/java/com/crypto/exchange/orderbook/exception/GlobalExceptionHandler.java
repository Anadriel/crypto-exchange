package com.crypto.exchange.orderbook.exception;

import com.crypto.exchange.models.http.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        HttpStatusCode statusCode = HttpStatus.BAD_REQUEST;
        String message = ex.getMessage();
        log.info("HttpMessageNotReadableException was caught: '{}'", message);
        ErrorResponse errorResponse = new ErrorResponse(statusCode.value(), message);
        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
