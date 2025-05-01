package com.example.docconneting.common.exception.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // JWT 에러 코드 J

    // 400
    UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "J001", "지원되지 않는 JWT 토큰입니다."),

    // 401
    JWT_TOKEN_REQUIRED(HttpStatus.UNAUTHORIZED, "J002", "JWT 토큰이 필요합니다."),
    INVALID_JWT_FORMAT(HttpStatus.UNAUTHORIZED, "J003", "잘못된 JWT 형식입니다."),
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "J004", "유효하지 않은 JWT 서명입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "J005", "만료된 JWT 토큰입니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "J006", "유효하지 않은 JWT 토큰입니다."),

    // 회원 에러코드 U

    // 400
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "U001", "이미 존재하는 이메일입니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "U002", "기존 비밀번호와 동일한 비밀번호로 수정할 수 없습니다."),

    //404
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "존재하지 않는 회원입니다."),

    //500
    AUTH_WITHOUT_AUTHUSER(HttpStatus.INTERNAL_SERVER_ERROR, "U004", "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),

    // 의사 조회 에러코드 D

    //404
    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "D001", "존재하지 않는 의사입니다."),
    MAJOR_NOT_FOUND(HttpStatus.NOT_FOUND, "D002", "존재하지 않는 전공입니다."),

    //로그인 에러 코드 A

    //400
    USERROLE_NOT_FOUND(HttpStatus.BAD_REQUEST, "A001", "권한 이름을 잘못 입력하셨습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "A002", "비밀번호가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "A003", "리프레시 토큰이 일치하지 않습니다."),
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "A004", "이미지는 필수 입력 값입니다."),
    STARTTIME_NOT_FOUND(HttpStatus.BAD_REQUEST, "A005", "근무 시작 시간은 필수 입력 값입니다."),
    ENDTIME_NOT_FOUND(HttpStatus.BAD_REQUEST, "A006", "근무 종료 시간은 필수 입력 값입니다."),

    //401
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "A007", "의사만 접근 가능한 기능입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A008", "만료된 리프레시 토큰입니다."),

    //404
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "A009", "토큰이 존재하지 않습니다."),

    // 게시글 에러 코드 B

    // 400
    INVALID_PAY_TYPE(HttpStatus.BAD_REQUEST, "B001", "잘못된 결제 타입입니다."),
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "B002", "포인트가 부족합니다."),
    MISSING_COUPON_ID(HttpStatus.BAD_REQUEST, "B003", "유효하지 않은 쿠폰 아이디입니다."),

    // 403
    PATIENT_ONLY_ACCESS(HttpStatus.FORBIDDEN, "B004", "게시물 수정, 삭제는 환자만 접근 가능 합니다."),
    ONLY_AUTHOR_CAN_UPDATE_OR_DELETED(HttpStatus.FORBIDDEN, "B005", "게시물은 작성자만 수정 혹은 삭제 가능합니다."),

    // 404
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "B006", "게시글을 찾을 수 없습니다."),

    // 답글 에러코드 R

    // 404
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "답글을 찾을 수 없습니다."),

    // 403
    NOT_COMMENT_OWNER(HttpStatus.FORBIDDEN, "R002", "답글을 단 사용자가 아닙니다."),
    NOT_ALLOWED_TO_COMMENT(HttpStatus.FORBIDDEN, "R003", "답글을 달 수 있는 권한이 없습니다."),

    // 쿠폰 에러코드 C

    // 400
    COUPON_ALREADY_ISSUED(HttpStatus.BAD_REQUEST, "C001", "이미 발급된 쿠폰입니다."),
    COUPON_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "C002", "쿠폰 수량이 모두 소진되었습니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "C003", "사용 완료된 쿠폰입니다."),
    NO_AVAILABLE_USAGE(HttpStatus.BAD_REQUEST, "C004", "사용 가능한 횟수가 없습니다."),
    INVALID_COUPON_PERIOD(HttpStatus.BAD_REQUEST, "C005", "쿠폰 사용 기간이 아닙니다."),
    EXHAUSTED_COUPON(HttpStatus.BAD_REQUEST, "C006", "쿠폰 사용 횟수를 초과했습니다."),
    EXPIRED_COUPON(HttpStatus.BAD_REQUEST, "C007", "만료된 쿠폰입니다."),

    // 403
    SELF_COUPON_ONLY(HttpStatus.FORBIDDEN, "C008", "자기 쿠폰만 사용할 수 있습니다."),
    FORBIDDEN_PATIENT_ONLY(HttpStatus.FORBIDDEN, "C009", "환자만 쿠폰 발급을 할 수 있습니다."),
    FORBIDDEN_ADMIN_ONLY(HttpStatus.FORBIDDEN, "C010", "관리자만 사용할 수 있는 기능입니다."),

    // 404
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "C011", "존재하지 않는 쿠폰입니다."),

    // 409
    LOCK_ACQUISITION_FAILED(HttpStatus.CONFLICT, "C012", "락 획득에 실패했습니다."),

    // 500
    LOCK_INTERRUPTED(HttpStatus.INTERNAL_SERVER_ERROR, "C013", "락 획득 중 인터럽트가 발생했습니다."),


    // 채팅 에러코드 CH

    // 400
    INACTIVE_CHATTING_ROOM(HttpStatus.BAD_REQUEST, "CH001", "비활성화된 채팅방입니다."),

    // 403
    ONLY_PATIENT_CAN_CREATE_CHATTING_ROOM(HttpStatus.FORBIDDEN, "CH002", "환자만 채팅방을 만들 수 있습니다."),
    FORBIDDEN_CHATTING_ROOM_ACCESS(HttpStatus.FORBIDDEN, "CH003", "해당 채팅방에 대한 접근 권한이 없습니다."),

    // 404
    CHATTING_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CH004", "존재하지 않는 채팅방입니다."),

    // 409
    CHATTING_ROOM_ALREADY_EXIST(HttpStatus.CONFLICT, "CH005", "이미 환자와 의사의 채팅방이 존재합니다."),

    // 주문 에러코드 O

    // 400
    INVALID_ORDER_PRICE(HttpStatus.BAD_REQUEST, "O001", "유효하지 않은 금액입니다."),
    INVALID_ORDER_TYPE(HttpStatus.BAD_REQUEST, "O002", "유효하지 않은 주문 타입입니다."),
    INVALID_ORDER_PRODUCT(HttpStatus.BAD_REQUEST, "O003", "유효하지 않은 상품입니다."),

    // 403
    NOT_ALLOWED_TO_ORDER(HttpStatus.FORBIDDEN, "O004", "주문을 할 수 있는 권한이 없습니다."),
    FORBIDDEN_ORDER_ACCESS(HttpStatus.FORBIDDEN, "O005", "주문을 조회할 수 없습니다."),

    // 404
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O006", "존재하지 않는 주문입니다."),
    ORDER_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "O007", "존재하지 않는 주문상품입니다."),


    // 알람 에러코드 N

    // 429
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "N001", "요청이 너무 많아 더 이상 요청을 처리할 수 없습니다."),

    // 결제 에러코드 P

    // 400
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "P001", "지원하지 않는 결제 상태입니다."),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "P002", "지원하지 않는 결제 방식입니다."),
    PAYMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "P003", "결제가 완료되지 않았습니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "P004", "결제 금액이 불일치합니다."),

    // 402
    PAYMENT_VERIFICATION_FAILED(HttpStatus.PAYMENT_REQUIRED, "P005", "결제 검증에 실패했습니다."),

    // 500
    PAYMENT_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "P006", "결제 서버 오류입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
