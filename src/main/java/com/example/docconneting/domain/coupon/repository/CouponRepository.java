package com.example.docconneting.domain.coupon.repository;

import com.example.docconneting.domain.coupon.entity.Coupon;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "2000")})
    Optional<Coupon> findByIdWithPessimisticLock(@Param("id") Long id);
}
