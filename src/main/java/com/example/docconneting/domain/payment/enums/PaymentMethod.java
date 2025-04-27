package com.example.docconneting.domain.payment.enums;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;

public enum PaymentMethod {
    KAKAOPAY,
    TOSS_PAYMENTS,
    TEST;

    public static PaymentMethod of(String pgProvider, String method) {
        if (pgProvider == null || method == null) {
            throw new ClientException(ErrorCode.INVALID_PAYMENT_METHOD);
        }

        return switch (pgProvider.toLowerCase()) {
            case "kakaopay", "kakao" -> KAKAOPAY;
            case "tosspayments", "uplus" -> TOSS_PAYMENTS;
            default -> {
                System.out.println("⚠️ Invalid PG provider or method: " + pgProvider + " / " + method);
                throw new ClientException(ErrorCode.INVALID_PAYMENT_METHOD);
            }
        };
    }
}

