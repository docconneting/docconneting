package com.example.docconneting.domain.coupon.entity;

import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_coupons")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    private LocalDateTime endDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private PatientCoupon(User user, Coupon coupon, Integer availableCount, LocalDateTime endDate) {
        this.user = user;
        this.coupon = coupon;
        this.availableCount = availableCount;
        this.endDate = endDate;
    }
}
