package com.example.docconneting.domain.coupon.entity;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.coupon.enums.CouponStatus;
import com.example.docconneting.domain.user.entity.User;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private PatientCoupon(User user, Coupon coupon, Integer availableCount, LocalDateTime startDate, LocalDateTime endDate, CouponStatus status) {
        this.user = user;
        this.coupon = coupon;
        this.availableCount = availableCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public static PatientCoupon of(User user, Coupon coupon, Integer availableCount, LocalDateTime startDate, LocalDateTime endDate, CouponStatus status) {
        return new PatientCoupon(user, coupon, availableCount, startDate, endDate, status);
    }

    // 사용가능횟수 0이상일 때만 사용가능, 사용 후 0 되면 USED로 상태 변경
    public void use() {
        if (this.availableCount <= 0) {
            throw new ClientException(ErrorCode.COUPON_ALREADY_USED);
        }

        this.availableCount--;
        if (this.availableCount == 0) {
            this.status = CouponStatus.USED;
        }
    }

    // 기간 지났을 때 만료로 수정
    public void markAsExpired() {
        this.status = CouponStatus.EXPIRED;
    }

    // 쿠폰 상태는 ISSUED인데, 만료일이 지났으면 EXPIRED 처리
    public void updateStatusIfExpired() {
        if (this.status == CouponStatus.ISSUED && LocalDateTime.now().isAfter(this.endDate)) {
            this.status = CouponStatus.EXPIRED;
        }
    }
}
