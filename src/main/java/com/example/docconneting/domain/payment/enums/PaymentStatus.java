package com.example.docconneting.domain.payment.enums;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;

public enum PaymentStatus {
    REQUESTED, // 결제 요청
    COMPLETED, // 결제 완료
    FAILED;// 결제 실패

    public static PaymentStatus of(String value) {
        if (value == null) {
            throw new ClientException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        return switch (value.toLowerCase()) {
            case "requested", "ready" -> REQUESTED;
            case "paid" -> COMPLETED;
            case "failed" -> FAILED;
            default -> throw new ClientException(ErrorCode.INVALID_PAYMENT_STATUS);
        };
    }
}
