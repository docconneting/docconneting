package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.common.exception.object.ServerException;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.response.IssueCouponResponse;
import com.example.docconneting.domain.coupon.dto.response.PatientCouponResponse;
import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.entity.CouponHistory;
import com.example.docconneting.domain.coupon.entity.PatientCoupon;
import com.example.docconneting.domain.coupon.repository.CouponHistoryRepository;
import com.example.docconneting.domain.coupon.repository.CouponRepository;
import com.example.docconneting.domain.coupon.repository.PatientCouponRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientCouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final PatientCouponRepository patientCouponRepository;
    private final CouponHistoryRepository couponHistoryRepository;

    // 검증 후 쿠폰 발급
    @Transactional
    public IssueCouponResponse issue(AuthUser authUser, Long couponId) {

        if (authUser.getUserRole().equals(UserRole.PATIENT)) {
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

    // 쿠폰 목록 조회
    @Transactional
    public PageResult<PatientCouponResponse> findAllUserCoupons(Pageable pageable, AuthUser authUser) {

        User user = userRepository.findByPatientId(authUser.getId()).orElseThrow(
                () -> new ClientException(ErrorCode.USER_NOT_FOUND)
        );

        // 환자만 쿠폰 조회 가능
        if (user.getUserRole().equals(UserRole.PATIENT)) {
            throw new ClientException(ErrorCode.FORBIDDEN_PATIENT_ONLY);
        }

        Page<PatientCoupon> coupons = patientCouponRepository.findAllByUserId(pageable, user.getId());

        List<PatientCoupon> content = coupons.getContent();
        Pageable patientCouponsPageable = coupons.getPageable();

        List<PatientCouponResponse> patientCoupons = PatientCouponResponse.toPatientCouponResponses(content);

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(patientCouponsPageable.getPageNumber())
                .pageSize(patientCouponsPageable.getPageSize())
                .totalElement(coupons.getTotalElements())
                .totalPage(coupons.getTotalPages())
                .build();

        return new PageResult<>(patientCoupons, pageInfo);
    }

    // 받은 쿠폰 사용 후 히스토리 저장
    @Transactional
    public void useCoupon(User user, Long couponId, Long postId) {

        PatientCoupon patientCoupon = patientCouponRepository.findPatientCouponByIdAndUserId(user.getId(), couponId).orElseThrow(
                () -> new ClientException(ErrorCode.COUPON_NOT_FOUND)
        );

        // 자기 쿠폰인지 확인
        if (!patientCoupon.getUser().getId().equals(user.getId())) {
            throw new ClientException(ErrorCode.SELF_COUPON_ONLY);
        }

        // 사용가능 횟수가 0이면 예외처리
        if (!patientCoupon.isAvailableCountValid()) {
            throw new ClientException(ErrorCode.NO_AVAILABLE_USAGE);
        }

        // 쿠폰 사용 기간 체크
        if (!patientCoupon.isValidPeriod()) {
            throw new ClientException(ErrorCode.INVALID_COUPON_PERIOD);
        }

        patientCoupon.decreaseAvailableCount();
        patientCouponRepository.save(patientCoupon);

        // 쿠폰 히스토리에 저장
        couponHistoryRepository.save(CouponHistory.of(patientCoupon, postId));
    }

}
