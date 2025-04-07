package com.example.docconneting.common.exception.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //회원 에러코드
    //400
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST,"기존 비밀번호와 동일한 비밀번호로 수정할 수 없습니다."),

    //404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

    //500
    AUTH_WITHOUT_AUTHUSER(HttpStatus.INTERNAL_SERVER_ERROR,"@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),

    //로그인 에러 코드
    //400
    USERROLE_NOT_FOUND(HttpStatus.BAD_REQUEST, "권한 이름을 잘못 입력하셨습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST,"리프레시 토큰이 일치하지 않습니다." ),
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "이미지는 필수 입력 값입니다."),
    STARTTIME_NOT_FOUND(HttpStatus.BAD_REQUEST, "근무 시작 시간은 필수 입력 값입니다."),
    ENDTIME_NOT_FOUND(HttpStatus.BAD_REQUEST, "근무 종료 시간은 필수 입력 값입니다."),

    //401
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "의사만 접근 가능한 기능입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"만료된 리프레시 토큰입니다."),

    //404
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"토큰이 존재하지 않습니다."),


    // 의사 조회 에러코드
    //404
    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 의사입니다."),
    MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 전공입니다.");

    private final HttpStatus status;
    private final String message;
}
