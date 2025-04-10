package com.example.docconneting.domain.order.enums;

public enum OrderStatus {
    REQUESTED, // 주문 생성 직후
    COMPLETED, // 결제까지 완료된 주문
    EXPIRED // 일정 시간 내 미결제로 만료 (포트원 결제 유효시간 초과)
}
