package com.example.cns.common.exception;

public class NotificationException extends BusinessException {
    public NotificationException(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
