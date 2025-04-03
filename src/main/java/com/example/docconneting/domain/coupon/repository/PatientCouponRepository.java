package com.example.docconneting.domain.coupon.repository;

import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientCouponRepository extends JpaRepository<PatientCoupon, Long> {
}
