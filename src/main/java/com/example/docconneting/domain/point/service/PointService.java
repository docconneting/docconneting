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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private static final int POST_POINT_COST = 1000;

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional(readOnly = true)
    public PointResponse findPoint(Long userId) {

       User user = userRepository.findById(userId).orElseThrow(() ->
               new ClientException(ErrorCode.USER_NOT_FOUND));

        return PointResponse.of(user.getPoint());
    }

    @Transactional
    public void usePoint(User user, Long postId) {

        validateHasPoint(user);
        user.decreasePoint(POST_POINT_COST);

        PointHistory pointHistory = PointHistory.of(
                user,
                postId,
                false,
                PointType.EXPENSE,
                POST_POINT_COST);
        pointHistoryRepository.save(pointHistory);
    }

    @Transactional
    public void refundPoint(Long userId, Long postId, int point) {

        User user = userRepository.findUserByIdAndUserRoleWithPessimisticLock(userId, UserRole.PATIENT).orElseThrow(() ->
                new ClientException(ErrorCode.USER_NOT_FOUND));

        user.refundPoint(point);

        PointHistory pointHistory = PointHistory.of(
                user,
                postId,
                true,
                PointType.INCOME,
                point);
        pointHistoryRepository.save(pointHistory);
    }

    // 결제할 수 있는 포인트를 가지고 있는지 검증
    private void validateHasPoint(User user) {
        if (user.getPoint() < POST_POINT_COST) {
            throw new ClientException(ErrorCode.INSUFFICIENT_POINT);
        }
    }
}
