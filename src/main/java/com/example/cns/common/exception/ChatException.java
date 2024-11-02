package com.example.cns.common.exception;

public class ChatException extends BusinessException {
    public ChatException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
