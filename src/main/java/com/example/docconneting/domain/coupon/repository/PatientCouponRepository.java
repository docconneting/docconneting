package com.example.docconneting.domain.coupon.repository;

import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientCouponRepository extends JpaRepository<PatientCoupon, Long> {

    Optional<PatientCoupon> findPatientCouponByIdAndUserId(Long couponId, Long userId);
}
