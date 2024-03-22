package com.example.cns.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {
    MEMBER_NOT_FOUND(6001, HttpStatus.BAD_REQUEST, "사용자가 존재하지 않습니다."),
    EMPTY_ACCESS_TOKEN(6001, BAD_REQUEST, "토큰이 비어있습니다."),
    MALFORMED_TOKEN(6001, UNAUTHORIZED, "손상된 토큰입니다"),
    FAIL_TOKEN(6001, UNAUTHORIZED, "검증에 실패한 토큰입니다."),
    UNSUPPORTED_TOKEN(6001, UNAUTHORIZED, "지원하지 않는 토큰입니다."),
    INVALID_TOKEN(6001, BAD_REQUEST, "유효하지 않은 토큰입니다"),
    INVALID_ROLE(6001, BAD_REQUEST, "유효하지 않은 사용자입니다"),

    EXPIRED_TOKEN(6001, UNAUTHORIZED, "만료된 토큰입니다."),

    DUPLICATE_EMAIL_EXISTS(6001, BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    DUPLICATE_USERNAME_EXISTS(6001, BAD_REQUEST, "이미 사용 중인 아이디입니다."),

    INVALID_PASSWORD(6001, BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INCORRECT_AUTHENTICATION_NUMBER(6001, UNAUTHORIZED, "인증번호가 일치하지 않습니다"),
    INVALID_EMAIL(6001, BAD_REQUEST, "인증에 실패하였습니다."),
    COMPANY_NOT_EXIST(3001, BAD_REQUEST, "회사가 존재하지 않습니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String message;
}
