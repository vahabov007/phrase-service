package com.vahabvahabov.phrase_service.exception;

import lombok.Data;

import java.time.Instant;

@Data
public class ApiError {
    private String errorMessage;
    private int status;
    private String errorCode;
    private Instant errorTime = Instant.now();

    public ApiError(String errorMessage, int status, String errorCode) {
        this.errorCode = errorCode;
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
