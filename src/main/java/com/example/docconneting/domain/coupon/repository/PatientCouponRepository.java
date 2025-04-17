package com.example.docconneting.domain.coupon.repository;

import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PatientCouponRepository extends JpaRepository<PatientCoupon, Long> {

    Optional<PatientCoupon> findPatientCouponByIdAndUserId(Long couponId, Long userId);

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    @Query("SELECT pc FROM PatientCoupon pc JOIN FETCH pc.coupon WHERE pc.user.id = :userId And pc.endDate > CURRENT_TIMESTAMP")
    Page<PatientCoupon> findAllByUserId(Pageable pageable, @Param("userId") Long userId);
}
