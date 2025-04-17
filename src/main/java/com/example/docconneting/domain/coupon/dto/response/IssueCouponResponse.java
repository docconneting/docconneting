package com.example.docconneting.domain.coupon.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class IssueCouponResponse {

    private final Long userId;
    private final Long couponId;
    private final Integer availableCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    private IssueCouponResponse(Long userId, Long couponId, Integer availableCount, LocalDateTime startDate, LocalDateTime endDate) {
        this.userId = userId;
        this.couponId = couponId;
        this.availableCount = availableCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static IssueCouponResponse of(Long userId, Long couponId, Integer availableCount, LocalDateTime startDate, LocalDateTime endDate) {
        return new IssueCouponResponse(userId, couponId, availableCount, startDate, endDate);
    }
}
