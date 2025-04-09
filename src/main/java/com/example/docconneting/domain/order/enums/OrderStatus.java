package com.example.docconneting.domain.order.enums;

public enum OrderStatus {
    REQUESTED, // 주문 생성 직후
    COMPLETED, // 결제 성공
    FAILED, // 결제 실패
    EXPIRED // 일정 시간 내 미결제로 만료 (포트원 결제 유효시간 초과)
}
