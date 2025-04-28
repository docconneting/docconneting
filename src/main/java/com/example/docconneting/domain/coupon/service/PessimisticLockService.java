package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.exception.object.ServerException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.response.IssueCouponResponse;
import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import com.example.docconneting.domain.coupon.repository.CouponHistoryRepository;
import com.example.docconneting.domain.coupon.repository.CouponRepository;
import com.example.docconneting.domain.coupon.repository.PatientCouponRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PessimisticLockService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PatientCouponRepository patientCouponRepository;

    @Transactional
    public IssueCouponResponse issueWithPessimisticLock(AuthUser authUser, Long couponId) {

        if (authUser.getUserRole() != UserRole.PATIENT) {
            throw new ClientException(ErrorCode.FORBIDDEN_PATIENT_ONLY);
        }

        User user = userRepository.findByPatientId(authUser.getId()).orElseThrow(
                () -> new ClientException(ErrorCode.USER_NOT_FOUND)
        );

        Coupon coupon = couponRepository.findByIdWithPessimisticLock(couponId).orElseThrow(
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
