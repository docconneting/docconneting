package com.example.docconneting.domain.point.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.point.dto.response.PointResponse;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("포인트 조회 성공")
    void findPoint() {
        // given
        long userId = 1L;
        int point = 1000;

        User user = User.of(null, null, null, null, null, null);
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "point", point);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        PointResponse response = pointService.findPoint(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPoint()).isEqualTo(point);
    }

    @Test
    @DisplayName("유저 조회 실패")
    void UserNotFoundExceptionTest() {
        // given
        long userId = 1L;
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        ClientException thrown = assertThrows(ClientException.class, () -> pointService.findPoint(userId));
        assertThat(HttpStatus.NOT_FOUND).isEqualTo(thrown.getErrorCode().getStatus());
        assertThat(ErrorCode.USER_NOT_FOUND.getMessage()).isEqualTo(thrown.getErrorCode().getMessage());
    }
}