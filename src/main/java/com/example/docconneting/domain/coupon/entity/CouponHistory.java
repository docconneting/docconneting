package com.example.docconneting.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CouponHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patients_coupon_id")
    private PatientCoupon patientCoupon;

    private Long postId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime userAt;

    private CouponHistory(PatientCoupon patientCoupon, Long postId) {
        this.patientCoupon = patientCoupon;
        this.postId = postId;
    }

    public static CouponHistory of(PatientCoupon patientCoupon, Long postId) {
        return new CouponHistory(patientCoupon, postId);
    }
}
