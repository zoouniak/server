package com.example.cns.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {
    // 인증 1000번대
    EMPTY_ACCESS_TOKEN(1001, BAD_REQUEST, "토큰이 비어있습니다."),
    EXPIRED_TOKEN(1002, UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN(1003, BAD_REQUEST, "유효하지 않은 토큰입니다."),
    INVALID_ROLE(1004, BAD_REQUEST, "유효하지 않은 사용자입니다."),
    UNAUTHORIZED_USER(1005, UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    FORBIDDEN_REQUEST(1006, FORBIDDEN, "허가되지 않은 요청입니다."),
    INVALID_EMAIL_FORMAT(1007, BAD_REQUEST, "회사 이메일과 일치하지 않습니다."),
    DUPLICATE_EMAIL_EXISTS(1008, BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    DUPLICATE_USERNAME_EXISTS(1009, BAD_REQUEST, "이미 사용 중인 아이디입니다."),
    INVALID_PASSWORD(1010, BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INCORRECT_AUTHENTICATION_NUMBER(1011, UNAUTHORIZED, "인증번호가 일치하지 않습니다."),
    INVALID_EMAIL(1012, BAD_REQUEST, "인증에 실패하였습니다."),
    INCORRECT_INFO(1013, BAD_REQUEST, "회원정보와 일치하지 않습니다."),
    // 회원 2000번대
    MEMBER_NOT_FOUND(2001, BAD_REQUEST, "사용자가 존재하지 않습니다."),
    COMPANY_NOT_EXIST(2002, BAD_REQUEST, "회사가 존재하지 않습니다."),

    //게시글, 댓글
    POST_NOT_EXIST(3001, BAD_REQUEST, "게시글이 존재하지 않습니다."),
    COMMENT_NOT_EXIST(3002, BAD_REQUEST, "댓글이 존재하지 않습니다."),
    IMAGE_UPDATE_FAILED(3000, BAD_REQUEST, "이미지 수정에 실패하였습니다."),

    // 채팅 4000번대
    ChatROOM_NOT_EXIST(4001, BAD_REQUEST, "채팅방이 존재하지 않습니다"),
    NOT_PARTICIPANTS(4002, BAD_REQUEST, "해당 채팅방의 회원이 아닙니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String message;
}
