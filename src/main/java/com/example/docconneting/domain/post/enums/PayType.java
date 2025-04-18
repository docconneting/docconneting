package com.example.docconneting.domain.post.enums;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;

import java.util.Arrays;

public enum PayType {
    FREE,
    POINT,
    COUPON;

    public static PayType of(String type) {
        return Arrays.stream(PayType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new ClientException(ErrorCode.INVALID_PAY_TYPE));
    }
}
