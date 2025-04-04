package com.example.docconneting.common.exception.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 회원 에러코드
    // 403
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),

    // 의사 조회 에러코드
    //404
    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 의사입니다.");

    private final HttpStatus status;
    private final String message;
}
