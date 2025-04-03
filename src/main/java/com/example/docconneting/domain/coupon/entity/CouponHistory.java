package com.example.docconneting.domain.coupon.entity;

import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patients_coupon_id")
    private PatientCoupon patientCoupon;

    private Long postId;

    private LocalDateTime userAt;

    @Builder
    public CouponHistory(PatientCoupon patientCoupon, Long postId, LocalDateTime userAt) {
        this.patientCoupon = patientCoupon;
        this.postId = postId;
        this.userAt = userAt;
    }
}
