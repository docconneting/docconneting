package com.example.docconneting.domain.coupon.dto.response;

import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PatientCouponResponse {

    private final Long couponId;
    private final Integer availableCount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    private PatientCouponResponse(Long couponId, Integer availableCount, LocalDateTime startDate, LocalDateTime endDate) {
        this.couponId = couponId;
        this.availableCount = availableCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static PatientCouponResponse of(Long couponId, Integer availableCount, LocalDateTime startDate, LocalDateTime endDate) {
        return new PatientCouponResponse(couponId, availableCount, startDate, endDate);
    }

    public static List<PatientCouponResponse> toPatientCouponResponses(List<PatientCoupon> patientCoupons) {
        return patientCoupons.stream().map(patientCoupon ->
                        new PatientCouponResponse(
                                patientCoupon.getCoupon().getId(),
                                patientCoupon.getAvailableCount(),
                                patientCoupon.getStartDate(),
                                patientCoupon.getEndDate())
                )
                .collect(Collectors.toList());
    }
}
