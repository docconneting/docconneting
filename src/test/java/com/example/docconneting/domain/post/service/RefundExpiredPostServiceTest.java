package com.example.docconneting.domain.post.service;

import com.example.docconneting.common.enums.Major;
import com.example.docconneting.domain.point.service.PointService;
import com.example.docconneting.domain.post.entity.Post;
import com.example.docconneting.domain.post.enums.PayType;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RefundExpiredPostServiceTest {

    @Mock
    FindExpiredPostService findExpiredPostService;

    @Mock
    PointService pointService;

    @InjectMocks
    RefundExpiredPostService refundExpiredPostService;

    @Test
    @DisplayName("만료된 유료 게시판을 조회 후 환불 성공")
    void postRefundTest() {
        // given
        Long userId = 1L;
        int point = 0;

        User user = User.of("test@test.com", "test", "testpatient", point, false, UserRole.PATIENT);
        ReflectionTestUtils.setField(user, "id", userId);

        List<Post> expiredPosts = List.of(
                Post.of(
                        user,
                        "title1",
                        "contents1",
                        Major.DERMATOLOGY,
                        PayType.POINT,
                        false,
                        false,
                        LocalDateTime.now().minusDays(1)),
                Post.of(
                        user,
                        "title2",
                        "contents2",
                        Major.DERMATOLOGY,
                        PayType.COUPON,
                        false,
                        false,
                        LocalDateTime.now().minusDays(1))
        );

        given(findExpiredPostService.findExpiredPosts()).willReturn(expiredPosts);

        // when
        refundExpiredPostService.postRefund();

        // then
        assertThat(expiredPosts.get(0).getPayType()).isEqualTo(PayType.FREE);
        assertThat(expiredPosts.get(1).getPayType()).isEqualTo(PayType.FREE);
    }

}