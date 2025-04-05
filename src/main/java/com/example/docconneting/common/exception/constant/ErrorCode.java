package com.example.docconneting.common.exception.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 회원 에러코드

    // 게시물 에러 코드

    // 404 NOT_FOUND
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "존재하지 않는 게시물 입니다."),

    // 의사 조회 에러코드

    //404
    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 의사입니다."),
    MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 전공입니다.");

    private final HttpStatus status;
    private final String message;
}
