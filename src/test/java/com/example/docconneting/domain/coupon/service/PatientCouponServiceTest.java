package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.entity.Coupon;
import com.example.docconneting.domain.coupon.repository.CouponRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class PatientCouponServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private DistributedCouponService distributedCouponService;

    private Long couponId;

    @BeforeEach
    void setUp() {
        Coupon coupon = couponRepository.save(
                Coupon.of(
                        1,
                        10,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(7)
                )
        );
        couponId = coupon.getId();
    }

    @Test
    void 동시에_쿠폰10개를_여러_사람이_요청하면_초과_발급되지_않는다() throws InterruptedException {
        int testCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(testCount);
        AtomicInteger successfulUpdates = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < testCount; i++) {
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

            executorService.execute(() -> {
                try {
                    distributedCouponService.issueCoupon(authUser, couponId);
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

        System.out.println("성공 수: " + successfulUpdates.get());
        System.out.println("실패 수: " + failCount.get());

        Assertions.assertThat(successfulUpdates.get()).isEqualTo(10);
    }
}