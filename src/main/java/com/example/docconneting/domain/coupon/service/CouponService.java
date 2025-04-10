package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.request.CreateCouponRequest;
import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.repository.CouponRepository;
import com.example.docconneting.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    // 쿠폰 만료기간은 생성 시점부터 7일
    private static final int COUPON_VALID_DAYS = 7;
    // 한 쿠폰 당 이용 가능 횟수.
    private static final int DEFAULT_AVAILABLE_COUNT = 5;


    // 쿠폰 생성
    public void createCoupon(AuthUser authUser, CreateCouponRequest request) {

        //운영자인지 확인
        if (authUser.getUserRole() != UserRole.ADMIN) {
            throw new ClientException(ErrorCode.FORBIDDEN_ADMIN_ONLY);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusDays(COUPON_VALID_DAYS);

        Coupon coupon = Coupon.of(
                DEFAULT_AVAILABLE_COUNT,
                request.getQuantity(),
                now,
                expiredAt
        );
        couponRepository.save(coupon);
    }
}
