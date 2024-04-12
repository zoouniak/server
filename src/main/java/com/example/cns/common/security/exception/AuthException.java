package com.example.cns.common.security.exception;

import com.example.cns.common.exception.ExceptionCode;
import org.springframework.http.HttpStatus;

public class AuthException extends RuntimeException {
    private final ExceptionCode exceptionCode;

    public AuthException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public HttpStatus getStatus() {
        return exceptionCode.getHttpStatus();
    }

    public int getErrorCode() {
        return exceptionCode.getErrorCode();
    }
}
