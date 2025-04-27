package com.example.docconneting.domain.point.service;

import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
public class CreatePostDistributedLockTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("포인트 환불과 동시에 게시글 등록 상황에서 분산락으로 정합성 보장 테스트")
    void distributedLockTest() throws InterruptedException{
        // given
        User user = User.of("test@example.com", "password", "username", 1000, false, UserRole.PATIENT);
        userRepository.save(user);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // when
        executorService.submit(() -> {
            try {
                pointService.usePoint(user.getId(), 1L);
            } catch (Exception e) {
                System.out.println("포인트 사용 실패: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                pointService.refundPoint(user.getId(), 3L, 1000); // 임의의 postId
            } catch (Exception e) {
                System.out.println("포인트 환불 실패: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        // then
        User updateduser = userRepository.findById(user.getId()).orElseThrow();
        int point = updateduser.getPoint();

        // 환불 1000
        assertThat(point).isGreaterThanOrEqualTo(1000);
        assertThat(point).isEqualTo(1000);
    }

}
