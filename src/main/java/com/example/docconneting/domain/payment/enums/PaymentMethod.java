package com.example.docconneting.domain.payment.enums;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;

public enum PaymentMethod {
    KAKAOPAY, // 카카오페이 간편결제
    TOSS_PAYMENTS; // 토스 페이먼츠 일반결제

    public static PaymentMethod of(String method) {
        return switch (method.toLowerCase()) {
            case "kakaopay" -> KAKAOPAY;
            case "tosspayments" -> TOSS_PAYMENTS;
            default -> throw new ClientException(ErrorCode.INVALID_PAYMENT_METHOD);
        };
    }
}
