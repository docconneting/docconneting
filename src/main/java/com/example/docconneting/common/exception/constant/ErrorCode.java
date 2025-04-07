package com.example.docconneting.common.exception.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 회원 에러코드

    // 401
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // 403
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),

    // 게시글 에러코드

    // 401
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),

    // 답글 에러코드

    // 401
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "답글을 찾을 수 없습니다."),

    // 403
    NOT_COMMENT_OWNER(HttpStatus.FORBIDDEN, "답글을 단 사용자가 아닙니다."),
    NOT_ALLOWED_TO_COMMENT(HttpStatus.FORBIDDEN, "답글을 달 수 있는 권한이 없습니다."),


    // 의사 조회 에러코드

    //404
    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 의사입니다."),
    MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 전공입니다.");

    private final HttpStatus status;
    private final String message;
}
