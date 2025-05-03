package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.exception.object.ServerException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.response.IssueCouponResponse;
import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import com.example.docconneting.domain.coupon.repository.CouponRepository;
import com.example.docconneting.domain.coupon.repository.PatientCouponRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OptimisticLockService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PatientCouponRepository patientCouponRepository;

    // 최대 재시도 횟수
    private static final int MAX_RETRIES = 1;

    // 재시도 간 대기 시간 (단위: 밀리초)
    private static final int RETRY_DELAY_MS = 1000;

    // 재시도 3번만 허용, 재시도 후 1초 동안 대기 후 재시도
    public IssueCouponResponse issueWithOptimisticLock(AuthUser authUser, Long couponId) {
        int retryCount = 0;
        boolean success = false;
        IssueCouponResponse response = null;

        while (retryCount < MAX_RETRIES && !success) {
            try {
                response = tryIssueCoupon(authUser, couponId);
                success = true;
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount >= MAX_RETRIES) {
                    throw new IllegalStateException("최대 재시도 횟수를 초과했습니다.");
                }
                try {
                    // 재시도 전에 대기 (Exponential Backoff 등 사용 가능)
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도중 인터럽트가 발생했습니다.");
                }
            }
        }
        return response;
    }

    // 검증 후 쿠폰 발급
    @Transactional
    public IssueCouponResponse tryIssueCoupon(AuthUser authUser, Long couponId) {

        if (authUser.getUserRole() != UserRole.PATIENT) {
            throw new ClientException(ErrorCode.FORBIDDEN_PATIENT_ONLY);
        }

        User user = userRepository.findByPatientId(authUser.getId()).orElseThrow(
                () -> new ClientException(ErrorCode.USER_NOT_FOUND)
        );

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new ServerException(ErrorCode.COUPON_NOT_FOUND)
        );

        // 만료된 쿠폰인지 확인
        if (!coupon.isAvailable(LocalDateTime.now())) {
            throw new ServerException(ErrorCode.EXPIRED_COUPON);
        }

        // 발급 받았었는지 확인
        boolean alreadyIssued = patientCouponRepository.existsByUserIdAndCouponId(authUser.getId(), couponId);
        if (alreadyIssued) {
            throw new ClientException(ErrorCode.COUPON_ALREADY_ISSUED);
        }

        // 운영자가 생성한 쿠폰 수 차감
        if (coupon.getQuantity() <= 0) {
            throw new ServerException(ErrorCode.COUPON_OUT_OF_STOCK);
        }

        coupon.decreaseQuantity();
        couponRepository.save(coupon);

        PatientCoupon patientCoupon = PatientCoupon.of(user, coupon, coupon.getAvailableCount(), coupon.getStartDate(), coupon.getEndDate());
        patientCouponRepository.save(patientCoupon);

        return IssueCouponResponse.of(user.getId(), coupon.getId(), coupon.getAvailableCount(), coupon.getStartDate(), coupon.getEndDate());
    }
}
