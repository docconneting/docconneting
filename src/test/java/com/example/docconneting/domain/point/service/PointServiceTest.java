package com.example.docconneting.domain.point.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.point.dto.response.PointResponse;
import com.example.docconneting.domain.point.entity.PointHistory;
import com.example.docconneting.domain.point.enums.PointType;
import com.example.docconneting.domain.point.repository.PointHistoryRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    @Nested
    class FindPoint {
        @Test
        @DisplayName("포인트 조회 성공")
        void findPoint() {
            // given
            long userId = 1L;
            int point = 1000;

            User user = User.of(
                    "email",
                    "password",
                    "username",
                    point,
                    false,
                    UserRole.PATIENT);
            ReflectionTestUtils.setField(user, "id", userId);

            given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

            // when
            PointResponse response = pointService.findPoint(userId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getPoint()).isEqualTo(point);
        }

        @Test
        @DisplayName("포인트 조회 유저 조회 실패")
        void userNotFoundExceptionTest() {
            // given
            long userId = 1L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when, then
            ClientException thrown = assertThrows(ClientException.class, () -> pointService.findPoint(userId));
            assertThat(HttpStatus.NOT_FOUND).isEqualTo(thrown.getErrorCode().getStatus());
            assertThat(ErrorCode.USER_NOT_FOUND.getMessage()).isEqualTo(thrown.getErrorCode().getMessage());
        }
    }

    @Nested
    class UserPoint {
        @Test
        @DisplayName("포인트 사용 성공")
        void usePointTest() {
            // given
            Long userId = 1L;
            Long postId = 1L;
            int point = 1000;
            int resultPoint = 0;

            User user = User.of(
                    "test@example.com",
                    "password",
                    "username",
                    point,
                    false,
                    UserRole.PATIENT);

            PointHistory pointHistory = PointHistory.of(
                    user,
                    postId,
                    false,
                    PointType.EXPENSE,
                    point
            );

            given(userRepository.findUserByIdAndUserRole(userId, UserRole.PATIENT)).willReturn(Optional.of(user));
            given(pointHistoryRepository.save(any(PointHistory.class))).willReturn(pointHistory);

            // when
            pointService.usePoint(userId, postId);

            // then
            assertThat(resultPoint).isEqualTo(user.getPoint());
        }

        @Test
        @DisplayName("포인트 사용 유저 조회 실패")
        void usePointUserNotFoundExceptionTest() {
            // given
            long userId = 1L;
            Long postId = 1L;
            int point = 1000;

            given(userRepository.findUserByIdAndUserRole(userId, UserRole.PATIENT)).willReturn(Optional.empty());

            // when, then
            ClientException thrown = assertThrows(ClientException.class, () -> pointService.usePoint(userId, postId));
            assertThat(HttpStatus.NOT_FOUND).isEqualTo(thrown.getErrorCode().getStatus());
            assertThat(ErrorCode.USER_NOT_FOUND.getMessage()).isEqualTo(thrown.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("포인트가 부족해서 실패")
        void insufficientPointBadRequestExceptionTest() {
            // given
            Long userId = 1L;
            Long postId = 1L;
            User user = User.of(
                    "test@example.com",
                    "password",
                    "username",
                    0,
                    false,
                    UserRole.PATIENT);

            given(userRepository.findUserByIdAndUserRole(userId, UserRole.PATIENT)).willReturn(Optional.of(user));

            // when, then
            ClientException thrown = assertThrows(ClientException.class, () -> pointService.usePoint(userId, postId));
            assertThat(HttpStatus.BAD_REQUEST).isEqualTo(thrown.getErrorCode().getStatus());
            assertThat(ErrorCode.INSUFFICIENT_POINT.getMessage()).isEqualTo(thrown.getErrorCode().getMessage());
        }
    }

    @Nested
    class RefundPoint {
        @Test
        @DisplayName("포인트 환불 성공")
        void refundPointTest() {
            // given
            Long userId = 1L;
            Long postId = 1L;
            int point = 1000;
            int resultPoint = 2000;

            User user = User.of(
                    "test@example.com",
                    "password",
                    "username",
                    point,
                    false,
                    UserRole.PATIENT);

            PointHistory pointHistory = PointHistory.of(
                    user,
                    postId,
                    true,
                    PointType.INCOME,
                    point
            );

            given(userRepository.findUserByIdAndUserRole(userId, UserRole.PATIENT)).willReturn(Optional.of(user));
            given(pointHistoryRepository.save(any(PointHistory.class))).willReturn(pointHistory);

            // when
            pointService.refundPoint(userId, postId, point);

            // then
            assertThat(resultPoint).isEqualTo(user.getPoint());
        }

        @Test
        @DisplayName("포인트 조회 유저 조회 실패")
        void userNotFoundExceptionTest() {
            // given
            long userId = 1L;
            Long postId = 1L;
            int point = 1000;

            given(userRepository.findUserByIdAndUserRole(userId, UserRole.PATIENT)).willReturn(Optional.empty());

            // when, then
            ClientException thrown = assertThrows(ClientException.class, () -> pointService.refundPoint(userId, postId, point));
            assertThat(HttpStatus.NOT_FOUND).isEqualTo(thrown.getErrorCode().getStatus());
            assertThat(ErrorCode.USER_NOT_FOUND.getMessage()).isEqualTo(thrown.getErrorCode().getMessage());
        }
    }
}