package com.example.docconneting.domain.coupon.repository;

import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatientCouponRepository extends JpaRepository<PatientCoupon, Long> {

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
    Optional<PatientCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

//    @EntityGraph(attributePaths = {"coupon"})
//    @Query("SELECT pc FROM PatientCoupon pc WHERE pc.user.id = :userId AND pc.endDate > CURRENT_TIMESTAMP")
    @Query("SELECT pc FROM PatientCoupon pc JOIN FETCH pc.coupon WHERE pc.user.id = :userId And pc.endDate > CURRENT_TIMESTAMP")
    Page<PatientCoupon> findAllByUserId(Pageable pageable, @Param("userId") Long userId);
}
