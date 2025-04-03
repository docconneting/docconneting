package com.example.docconneting.domain.coupon.entity;

import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private Integer availableCount;

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    private LocalDateTime createdAt;

    private LocalDateTime endDate;

    @Builder
    public PatientCoupon(User user, Coupon coupon, Integer availableCount, CouponStatus couponStatus, LocalDateTime createdAt, LocalDateTime endDate) {
        this.user = user;
        this.coupon = coupon;
        this.availableCount = availableCount;
        this.couponStatus = couponStatus;
        this.createdAt = createdAt;
        this.endDate = endDate;
    }
}
