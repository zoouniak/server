package com.example.cns.common.exception;

import com.example.cns.common.security.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleException(final BusinessException e) {
        log.error(e.getMessage());
        HttpStatusCode httpStatus = e.getStatus();

        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getErrorCode(), e.getMessage());

        return ResponseEntity.status(httpStatus).body(exceptionResponse);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> handleAuthException(final AuthException e) {
        log.error(e.getMessage());
        HttpStatusCode httpStatus = e.getStatus();

        ExceptionResponse exceptionResponse = new ExceptionResponse(e.getErrorCode(), e.getMessage());

        return ResponseEntity.status(httpStatus).body(exceptionResponse);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> handleAuthException(final IllegalStateException e) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(4002, "요청에 대한 권한이 없습니다.");
        return ResponseEntity.status(403).body(exceptionResponse);
    }
}
