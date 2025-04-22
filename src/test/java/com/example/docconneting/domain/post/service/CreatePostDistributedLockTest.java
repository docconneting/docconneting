package com.example.docconneting.domain.post.service;

import com.example.docconneting.domain.point.service.PointService;
import com.example.docconneting.domain.post.dto.request.PostCreateRequest;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CreatePostDistributedLockTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PointService pointService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("포인트 환불과 동시에 포인트 사용 상황에서 분산락으로 정합성 보장 테스트")
    void distributedLockTest() throws InterruptedException{
        // given
        Long postId1 = 3L;
        Long postId2 = 6L;
        User user = User.of("test@example.com", "password", "username", 1000, false, UserRole.PATIENT);
        userRepository.save(user);

        PostCreateRequest request = new PostCreateRequest();
        ReflectionTestUtils.setField(request, "title", "제목");
        ReflectionTestUtils.setField(request, "contents", "내용");
        ReflectionTestUtils.setField(request, "major", "ORTHOPEDICS");
        ReflectionTestUtils.setField(request, "payType", "POINT");

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // when
        executorService.submit(() -> {
            try {
                pointService.usePoint(user, postId1); // 임의의 postId
            } catch (Exception e) {
                System.out.println("포인트 사용 실패 " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                pointService.refundPoint(user.getId(), postId2, 1000); // 임의의 postId
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
