package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.exception.object.ServerException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.response.IssueCouponResponse;
import com.example.docconneting.domain.coupon.dto.response.PatientCouponResponse;
import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import com.example.docconneting.domain.coupon.enums.CouponStatus;
import com.example.docconneting.domain.coupon.repository.CouponRepository;
import com.example.docconneting.domain.coupon.repository.PatientCouponRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientCouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PatientCouponRepository patientCouponRepository;

    // 검증 후 쿠폰 발급
    @Transactional
    public IssueCouponResponse issue(AuthUser authUser, Long couponId) {

        if (authUser.getUserRole() != UserRole.PATIENT) {
            throw new ClientException(ErrorCode.FORBIDDEN_PATIENT_ONLY);
        }

        User user = userRepository.findById(authUser.getId()).get();

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new ServerException(ErrorCode.COUPON_NOT_FOUND)
        );

        // 발급 받았었는지 확인
        boolean alreadyIssued = patientCouponRepository.existsByUserIdAndCouponId(authUser.getId(), couponId);
        if (alreadyIssued) {
            throw new ClientException(ErrorCode.COUPON_ALREADY_ISSUED);
        }

        coupon.decreaseQuantity();
        couponRepository.save(coupon);

        PatientCoupon patientCoupon = PatientCoupon.of(user, coupon, coupon.getAvailableCount(), coupon.getStartDate(), coupon.getEndDate(), CouponStatus.ISSUED);
        patientCouponRepository.save(patientCoupon);

        return IssueCouponResponse.of(user.getId(), coupon.getId(), coupon.getAvailableCount(), coupon.getQuantity(), coupon.getStartDate(), coupon.getEndDate());
    }

    // 쿠폰 목록 조회
    @Transactional
//    @Transactional(readOnly = true) <- 쿠폰 상태 변경할 때 true로 돼있으면 변경 안됨.
    public List<PatientCouponResponse> getUserCoupons(AuthUser authUser) {

        // 환자만 쿠폰 조회 가능
        if (authUser.getUserRole() != UserRole.PATIENT) {
            throw new ClientException(ErrorCode.FORBIDDEN_PATIENT_ONLY);
        }

        List<PatientCoupon> coupons = patientCouponRepository.findAllByUserId(authUser.getId());
        List<PatientCouponResponse> responseList = new ArrayList<>();

        for (PatientCoupon coupon : coupons) {
            // 쿠폰 상태는 ISSUED인데, 만료일이 지났으면 EXPIRED 처리
            coupon.updateStatusIfExpired();

            PatientCouponResponse response = PatientCouponResponse.of(coupon.getId(), coupon.getAvailableCount(), coupon.getCoupon().getStartDate(), coupon.getEndDate(), coupon.getStatus());
            responseList.add(response);
        }
        return responseList;
    }

    // 받은 쿠폰 사용
    @Transactional
    public PatientCouponResponse useCoupon(AuthUser authUser, Long userCouponId) {

        PatientCoupon patientCoupon = patientCouponRepository.findById(userCouponId).orElseThrow(
                () -> new ClientException(ErrorCode.COUPON_NOT_FOUND)
        );

        // 자기 쿠폰인지 확인
        if (!patientCoupon.getUser().getId().equals(authUser.getId())) {
            throw new ClientException(ErrorCode.SELF_COUPON_ONLY);
        }

        // 사용 완료된 쿠폰인지 확인
        if (patientCoupon.getStatus() == CouponStatus.USED) {
            throw new ClientException(ErrorCode.COUPON_ALREADY_USED);
        }

        // 잔여 횟수 체크
        if (patientCoupon.getAvailableCount() <= 0) {
            throw new ClientException(ErrorCode.NO_AVAILABLE_USAGE);
        }

        // 쿠폰 사용 기간 체크
        if (LocalDateTime.now().isBefore(patientCoupon.getStartDate()) || LocalDateTime.now().isAfter(patientCoupon.getEndDate())) {
            throw new ClientException(ErrorCode.INVALID_COUPON_PERIOD);
        }

        patientCoupon.use();
        patientCouponRepository.save(patientCoupon);

        return PatientCouponResponse.of(
                patientCoupon.getCoupon().getId(),
                patientCoupon.getAvailableCount(),
                patientCoupon.getStartDate(),
                patientCoupon.getEndDate(),
                patientCoupon.getStatus()
        );
    }
}
