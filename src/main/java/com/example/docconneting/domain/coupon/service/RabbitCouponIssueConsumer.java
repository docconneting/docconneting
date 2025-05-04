package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.request.CouponIssueRequestMessage;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitCouponIssueConsumer {

    private final PatientCouponService patientCouponService;
    private final UserRepository userRepository;

    @Transactional
    @RabbitListener(queues = "${coupon.queue.coupon-name}")
    public void consumeCouponIssue(CouponIssueRequestMessage message) {
        log.info("받은 메세지: {}", message);

        try {
            User user = userRepository.findById(message.getUserId()).orElseThrow(
                    () -> new ClientException(ErrorCode.USER_NOT_FOUND)
            );
            AuthUser authUser = AuthUser.of(user.getId(), user.getUserRole());
            patientCouponService.issue(authUser, message.getCouponId());
        } catch (ClientException e) {
            // 불필요한 재시도 x
            log.warn("처리 불가한 클라이언트 예외: {}", e.getMessage());
            throw new AmqpRejectAndDontRequeueException(e);

        } catch (Exception e) {
            // 네트워크, DB 장애 등 시스템 예외 → 재시도
            log.error("처리 중 서버 예외 발생", e);
            throw e;
        }
    }
}
