package com.crypto.exchange.models.http;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;

import lombok.Getter;

@Getter
public class ErrorResponse implements Serializable {
    private int statusCode;
    private List<String> messages;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode, List<String> messages) {
        this.statusCode = statusCode;
        this.messages = messages;
    }

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.messages = Collections.singletonList(message);
    }

    public ResponseEntity<ErrorResponse> toResponseEntity() {
        return ResponseEntity.status(statusCode).body(this);
    }
}
