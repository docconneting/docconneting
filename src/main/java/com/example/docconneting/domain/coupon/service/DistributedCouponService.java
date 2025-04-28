package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.common.config.annotation.DistributedLock;
import com.example.docconneting.domain.auth.entity.AuthUser;
<<<<<<< HEAD
=======
//import com.example.docconneting.domain.coupon.annotation.DistributedLock;
>>>>>>> dev
import com.example.docconneting.domain.coupon.dto.response.IssueCouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributedCouponService {

    private final PatientCouponService patientCouponService;


    // 쿠폰 발급
    @DistributedLock(value = "#couponId")  // SpEL 사용해서 key 지정
    public IssueCouponResponse issueCoupon(AuthUser authUser, Long couponId) {
        return patientCouponService.issue(authUser, couponId);
    }
}
