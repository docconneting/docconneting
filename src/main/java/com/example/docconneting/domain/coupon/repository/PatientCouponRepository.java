package com.example.docconneting.domain.coupon.repository;

import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientCouponRepository extends JpaRepository<PatientCoupon, Long> {

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    @Query("SELECT pc FROM PatientCoupon pc JOIN FETCH pc.coupon WHERE pc.user.id = :userId")
    List<PatientCoupon> findAllByUserId(@Param("userId") Long userId);
}
