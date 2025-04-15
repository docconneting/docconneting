package com.example.docconneting.domain.coupon.controller;

import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.request.CreateCouponRequest;
import com.example.docconneting.domain.coupon.dto.response.CreateCouponResponse;
import com.example.docconneting.domain.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class AdminCouponController {

    private final CouponService couponService;

    // 운영자가 쿠폰 생성
    @PostMapping
    public ResponseEntity<Response<CreateCouponResponse>> createCoupon(
            @Auth AuthUser authUser,
            @Valid @RequestBody CreateCouponRequest request) {
        CreateCouponResponse createCouponResponse = couponService.createCoupon(authUser, request);
        return ResponseEntity.ok(Response.of(createCouponResponse));
    }
}
