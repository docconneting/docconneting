package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ServerException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.response.IssueCouponResponse;
import com.example.docconneting.domain.coupon.lock.RedissonDistributedLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class DistributedCouponService {

    private final RedissonDistributedLockManager distributedLockManager;
    private final PatientCouponService patientCouponService;


    // 쿠폰 발급
    public IssueCouponResponse issueCoupon(AuthUser authUser, Long couponId) {

        AtomicReference<IssueCouponResponse> responseHolder = new AtomicReference<>();

        try {
            // responseHolder.set(patientCouponService.issue(authUser, couponId));가 실행할 로직
            distributedLockManager.executeWithLock(couponId, () -> {
                responseHolder.set(patientCouponService.issue(authUser, couponId)); //같은 클래스 내에서 트렌잭션 없는 게 있는 걸 호출할 때 뜨는 에러.
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServerException(ErrorCode.LOCK_INTERRUPTED);
        }
        return responseHolder.get();
    }
}
