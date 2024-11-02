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
    FAIL_SEND_EMAIL(1014, INTERNAL_SERVER_ERROR, "이메일 전송에 실패하였습니다."),

    // 회원 2000번대
    MEMBER_NOT_FOUND(2001, BAD_REQUEST, "사용자가 존재하지 않습니다."),
    COMPANY_NOT_EXIST(2002, BAD_REQUEST, "회사가 존재하지 않습니다."),
    COMPANY_UPDATE_FAILED(2002, BAD_REQUEST, "회사 및 직무 수정에 실패하였습니다."),
    COMPANY_UPDATE_FORBIDDEN(2003, FORBIDDEN, "담당하고 있는 프로젝트가 있습니다."),
    RESUME_NOT_EXIST(2004, NOT_FOUND, "이력서가 존재하지 않습니다."),

    //게시글, 댓글, 파일 3000번대
    //3001 ~ 3100 게시글
    POST_NOT_EXIST(3001, NOT_FOUND, "게시글이 존재하지 않습니다."),
    NOT_POST_WRITER(3002, BAD_REQUEST, "게시글 작성자가 아닙니다."),
    INVALID_POST_INFO(3003, BAD_REQUEST, "잘못된 게시글 정보입니다."),

    //3101 ~ 3200 댓글
    COMMENT_NOT_EXIST(3101, NOT_FOUND, "댓글이 존재하지 않습니다."),
    NOT_COMMENT_WRITER(3102, BAD_REQUEST, "댓글 작성자가 아닙니다."),

    //3201 ~ 3300 파일
    NOT_SUPPORT_EXT(3201, UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 확장자입니다."),
    IMAGE_UPLOAD_FAILED(3202, BAD_REQUEST, "이미지 업로드에 실패하였습니다."),
    IMAGE_DELETE_FAILED(3203, BAD_REQUEST, "이미지 삭제에 실패하였습니다."),
    IMAGE_UPDATE_FAILED(3204, BAD_REQUEST, "이미지 수정에 실패하였습니다."),

    // 채팅 4000번대
    CHATROOM_NOT_EXIST(4001, BAD_REQUEST, "채팅방이 존재하지 않습니다"),
    NOT_CHAT_PARTICIPANTS(4002, BAD_REQUEST, "해당 채팅방의 회원이 아닙니다."),
    ROOM_CAPACITY_EXCEEDED(4003, BAD_REQUEST, "채팅방 수용 인원(10명)을 초과하였습니다."),
    PARTICIPANT_NOT_EXIST(4004, BAD_REQUEST, "해당 채팅 참여 정보가 존재하지 않습니다"),
    FAIL_SEND_INFOMSG(4005, BAD_REQUEST, "정보 메시지를 보내는데 실패하였습니다."),
    FAIL_SEND_MSG(4006, BAD_REQUEST, "메시지를 보내는데 실패하였습니다."),

    // 파일 5000번대
    FILE_NOT_SAVED(5001, INTERNAL_SERVER_ERROR, "파일 저장에 실패하였습니다."),
    FILE_NOT_SUPPORT(5002, BAD_REQUEST, "지원하지 않는 파일 형식입니다"),

    //프로젝트 6000번대
    PROJECT_NOT_EXIST(6001, NOT_FOUND, "프로젝트가 존재하지 않습니다."),
    MANAGER_CANNOT_LEAVE(6002, BAD_REQUEST, "담당자는 프로젝트를 나갈수 없습니다."),
    MANAGER_ONLY_ACTION(6003, FORBIDDEN, "담당자만 사용가능합니다."),
    MISMATCH_OPINION_TYPE(6004, BAD_REQUEST, "잘못된 의견 종류입니다."),
    NOT_PROJECT_PARTICIPANTS(4002, BAD_REQUEST, "해당 프로젝트의 참가자가 아닙니다."),
    // 할일 6500번대
    TASK_NOT_EXIST(6501, BAD_REQUEST, "해당 할 일이 존재하지 않습니다."),
    NOT_TASK_OWNER(6501, FORBIDDEN, "할 일의 주인이 아닙니다."),

    // 일정 7000번대
    PLAN_NOT_EXIST(7001, BAD_REQUEST, "일정이 존재하지 않습니다."),
    ONLY_MANAGER(7002, BAD_REQUEST, "담당자에게만 허용된 요청입니다"),
    PLAN_GENERATE_FAILED(7003, BAD_REQUEST,"일정 생성에 실패하였습니다."),

    // 외부 API 요청 8000번대
    FAIL_GET_API(8001, INTERNAL_SERVER_ERROR, "외부 API 요청에 실패하였습니다"),
    NOT_SUPPORT_ALARM(8002, BAD_REQUEST, "현재 지원하지 않는 알림 이벤트입니다."),
    FAIL_WITH_SSE(8003, INTERNAL_SERVER_ERROR, "SSE에서 실패하였습니다."),

    //화상회의 9000번대
    FAIL_CREATE_CHANNEL(9001, BAD_REQUEST, "채널 생성에 실패하였습니다."),
    CHANNEL_PARTICIPATION_EXIST(9002, FORBIDDEN, "채널에 사용자가 존재합니다."),
    CHANNEL_NOT_EXIST(9003, BAD_REQUEST, "채널이 존재하지 않습니다."),
    NOT_CHANNEL_PARTICIPATION(9004, BAD_REQUEST, "채널 참여자가 아닙니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String message;
}
