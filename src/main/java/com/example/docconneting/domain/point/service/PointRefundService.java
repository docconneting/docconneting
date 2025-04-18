package com.example.docconneting.domain.point.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
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
public class PointRefundService {

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

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
}
