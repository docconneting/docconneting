package com.example.docconneting.domain.coupon.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.response.IssueCouponResponse;
import com.example.docconneting.domain.coupon.dto.response.PatientCouponResponse;
import com.example.docconneting.domain.coupon.service.DistributedCouponService;
import com.example.docconneting.domain.coupon.service.PatientCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-coupons")
public class PatientCouponController {

    private final PatientCouponService patientCouponService;
    private final DistributedCouponService distributedCouponService;

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
    public ResponseEntity<Response<List<PatientCouponResponse>>> getUserCoupons(
            @Auth AuthUser authUser
    ) {
        List<PatientCouponResponse> responses = patientCouponService.getUserCoupons(authUser);
        return ResponseEntity.ok(Response.of(responses));
    }

    // 쿠폰 사용
    @PostMapping("/{userCouponId}/use")
    public ResponseEntity<Response<PatientCouponResponse>> useCoupon(
            @Auth AuthUser authUser,
            @PathVariable(name = "userCouponId") Long userCouponId
    ) {
        PatientCouponResponse response = patientCouponService.useCoupon(authUser, userCouponId);
        return ResponseEntity.ok(Response.of(response));
    }
}
