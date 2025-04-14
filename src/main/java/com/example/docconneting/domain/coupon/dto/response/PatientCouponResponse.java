package com.example.docconneting.domain.coupon.dto.response;

import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.enums.CouponStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PatientCouponResponse {

    private final Long couponId;
    private final Integer availableCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final CouponStatus status;

    private PatientCouponResponse(Long couponId, Integer availableCount, LocalDateTime startDate, LocalDateTime endDate, CouponStatus status) {
        this.couponId = couponId;
        this.availableCount = availableCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public static PatientCouponResponse of(Long couponId, Integer availableCount, LocalDateTime startDate, LocalDateTime endDate, CouponStatus status) {
        return new PatientCouponResponse(couponId, availableCount, startDate, endDate, status);
    }
}
