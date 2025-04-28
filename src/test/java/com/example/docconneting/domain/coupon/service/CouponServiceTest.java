package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.request.CreateCouponRequest;
import com.example.docconneting.domain.coupon.dto.response.CreateCouponResponse;
import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.repository.CouponRepository;
import com.example.docconneting.domain.user.enums.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private AuthUser authUser;
    private CreateCouponRequest request;
    private LocalDateTime now;
    private LocalDateTime expiredAt;

    // 쿠폰 만료기간은 생성 시점부터 7일
    private static final int COUPON_VALID_DAYS = 7;
    // 한 쿠폰 당 이용 가능 횟수.
    private static final int DEFAULT_AVAILABLE_COUNT = 5;

    @BeforeEach
    void setUp() {
        authUser = AuthUser.of(1L, UserRole.ADMIN);
        request = CreateCouponRequest.of(10);
        now = LocalDateTime.of(2025, 4, 18, 10, 0);
        expiredAt = now.plusDays(COUPON_VALID_DAYS);
    }

    private Coupon createsavedCoupon() {
        Coupon coupon = Coupon.of(
                DEFAULT_AVAILABLE_COUNT,
                request.getQuantity(),
                now,
                expiredAt
        );
        ReflectionTestUtils.setField(coupon, "id", 1L);
        return coupon;
    }



    @Test
    @DisplayName("운영자가 쿠폰 생성에 성공하면 쿠폰이 저장되고 응답이 반환된다.")
    void createCouponTest() {

        // given
        Coupon savedCoupon = createsavedCoupon();
//        when(couponRepository.save(savedCoupon)).thenReturn(savedCoupon);
        given(couponRepository.save(any(Coupon.class))).willReturn(savedCoupon);

        // when
        CreateCouponResponse response = couponService.createCoupon(authUser, request, now);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(savedCoupon.getId());
        assertThat(response.getAvailableCount()).isEqualTo(DEFAULT_AVAILABLE_COUNT);
        assertThat(response.getQuantity()).isEqualTo(request.getQuantity());
        assertThat(response.getStartDate()).isEqualTo(now);
        assertThat(response.getEndDate()).isEqualTo(expiredAt);
    }

    @Test
    @DisplayName("운영자외에 쿠폰 생성시 예외가 발생한다.")
    void createCouponByNonAdminThrowsException() {
        // given
        AuthUser nonAdmin = AuthUser.of(2L, UserRole.PATIENT);

        ClientException ex = assertThrows(ClientException.class, () ->
                couponService.createCoupon(nonAdmin, request, now)
        );

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_ADMIN_ONLY);
    }

    @Test
    @DisplayName("쿠폰 저장이 실제로 repository에 save로 호출됐는지 확인")
    void couponRepositorySaveCalledOnce() {

        // given
        Coupon savedCoupon = createsavedCoupon();
        given(couponRepository.save(any(Coupon.class))).willReturn(savedCoupon);

        // when
        couponService.createCoupon(authUser, request, now);

        // then
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }
}