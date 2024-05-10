package com.example.cns.common.security.exception;

import com.example.cns.common.exception.BusinessException;
import com.example.cns.common.exception.ExceptionCode;

public class AuthException extends BusinessException {

    public AuthException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
