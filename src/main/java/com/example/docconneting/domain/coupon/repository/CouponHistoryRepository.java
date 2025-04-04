package com.example.docconneting.domain.coupon.repository;

import com.example.docconneting.domain.coupon.entity.CouponHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponHistoryRepository extends JpaRepository<CouponHistory, Long> {
}
