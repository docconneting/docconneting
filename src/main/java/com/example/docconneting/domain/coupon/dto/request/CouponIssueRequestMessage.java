package com.example.docconneting.domain.coupon.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CouponIssueRequestMessage {
    private Long userId;
    private Long couponId;

    private CouponIssueRequestMessage(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }

    public static CouponIssueRequestMessage of(Long userId, Long couponId) {
        return new CouponIssueRequestMessage(userId, couponId);
    }
}
