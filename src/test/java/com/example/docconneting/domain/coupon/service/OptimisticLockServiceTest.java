package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.repository.CouponRepository;
import com.example.docconneting.domain.coupon.repository.PatientCouponRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OptimisticLockServiceTest {

    @Autowired
    private OptimisticLockService optimisticLockService;

    @Autowired
    private PatientCouponRepository patientCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    private Long couponId;

    @BeforeEach
    void setUp() {
        // 쿠폰 생성
        Coupon coupon = Coupon.of(5, 10, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        Coupon savedCoupon = couponRepository.save(coupon);
        this.couponId = savedCoupon.getId();
    }

    @Test
    void 동시에_100명이_쿠폰을_요청하면_10명만_발급된다() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successfulUpdates = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int userId = i;

            User user = userRepository.save(
                    User.of(
                            "test" + userId + "@email.com",
                            "1234",
                            "환자" + userId,
                            100,
                            false,
                            UserRole.PATIENT
                    )
            );
            AuthUser authUser = AuthUser.of(user.getId(), user.getUserRole());

            executorService.submit(() -> {
                try {
                    optimisticLockService.issueWithOptimisticLock(authUser, couponId);
                    successfulUpdates.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        long issuedCount = patientCouponRepository.count();
        System.out.println("성공 수: " + successfulUpdates.get());
        System.out.println("실패 수: " + failCount.get());

        Assertions.assertThat(successfulUpdates.get()).isEqualTo(10);
    }
}