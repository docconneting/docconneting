package com.example.docconneting.domain.coupon.entity;

import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

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

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime userAt;

    @Builder
    public CouponHistory(PatientCoupon patientCoupon, Long postId) {
        this.patientCoupon = patientCoupon;
        this.postId = postId;
    }
}
