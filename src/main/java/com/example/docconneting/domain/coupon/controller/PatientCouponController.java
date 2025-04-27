package com.example.docconneting.domain.coupon.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.response.IssueCouponResponse;
import com.example.docconneting.domain.coupon.dto.response.PatientCouponResponse;
import com.example.docconneting.domain.coupon.service.DistributedCouponService;
import com.example.docconneting.domain.coupon.service.OptimisticLockService;
import com.example.docconneting.domain.coupon.service.PatientCouponService;
import com.example.docconneting.domain.coupon.service.PessimisticLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-coupons")
public class PatientCouponController {

    private final PatientCouponService patientCouponService;
    private final DistributedCouponService distributedCouponService;
    private final OptimisticLockService optimisticLockService;
    private final PessimisticLockService pessimisticLockService;

    // 사용자가 쿠폰 발급
    @PostMapping("/{couponId}")
    public ResponseEntity<Response<IssueCouponResponse>> issueCoupon(
            @Auth AuthUser authUser,
            @PathVariable(name = "couponId") Long couponId
    ) {
        IssueCouponResponse response = distributedCouponService.issueCoupon(authUser, couponId);
        return ResponseEntity.ok(Response.of(response));
    }

    // 쿠폰 목록 조회
    @GetMapping
    public ResponseEntity<Response<List<PatientCouponResponse>>> findAllUserCoupons(
            @PageableDefault Pageable pageable,
            @Auth AuthUser authUser
    ) {
        PageResult<PatientCouponResponse> pageResult = patientCouponService.findAllUserCoupons(pageable, authUser);
        return ResponseEntity.ok().body(Response.of(pageResult.getContent(), pageResult.getPageInfo()));
    }

    // 낙관적 락 쿠폰 발급
    @PostMapping("/optimistic/{couponId}")
    public ResponseEntity<Response<IssueCouponResponse>> issueWithOptimisticLock(
            @Auth AuthUser authUser,
            @PathVariable(name = "couponId") Long couponId
    ) {
        IssueCouponResponse response = optimisticLockService.issueWithOptimisticLock(authUser, couponId);
        return ResponseEntity.ok(Response.of(response));
    }

    @PostMapping("/pessimistic/{couponId}")
    public ResponseEntity<Response<IssueCouponResponse>> issueWithPessimisticLock(
            @Auth AuthUser authUser,
            @PathVariable(name = "couponId") Long couponId
    ) {
        IssueCouponResponse response = pessimisticLockService.issueWithPessimisticLock(authUser, couponId);
        return ResponseEntity.ok(Response.of(response));
    }
}
