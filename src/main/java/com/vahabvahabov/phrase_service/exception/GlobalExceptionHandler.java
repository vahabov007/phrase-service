package com.vahabvahabov.phrase_service.exception;


import com.vahabvahabov.phrase_service.exception.exceptions.PhraseDefinitionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice @Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(PhraseDefinitionNotFoundException.class)
    public ResponseEntity<ApiError> handlePhraseDefinitionNotFoundException(PhraseDefinitionNotFoundException e) {
        log.warn("PhraseDefinitionNotFoundException occurred.");

        ApiError apiError = new ApiError(e.getMessage(),
                                         HttpStatus.NOT_FOUND.value(),
                                         "PHRASE_DEFINITION_NOT_FOUND");
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        log.warn("Exception occurred.");

        ApiError apiError = new ApiError(e.getMessage(),
                                         HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                         "INTERNAL_SERVER_ERROR");

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(Exception exception) {
        log.warn("Validation error occurred");

        ApiError error = new ApiError(exception.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
