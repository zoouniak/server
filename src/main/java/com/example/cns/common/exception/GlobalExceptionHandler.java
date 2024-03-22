package com.example.cns.common.exception;

import com.example.cns.common.security.exception.AuthException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleException(final BusinessException e) {
        HttpStatusCode httpStatus = e.getStatus();

        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getErrorCode(), e.getMessage());

        return ResponseEntity.status(httpStatus).body(exceptionResponse);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> handleAuthException(final AuthException e) {
        HttpStatusCode httpStatus = e.getStatus();

        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getErrorCode(), e.getMessage());

        return ResponseEntity.status(httpStatus).body(exceptionResponse);
    }
}
