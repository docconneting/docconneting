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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryConsumer {

    private final PatientCouponService patientCouponService;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.fail-key}")
    private String failRoutingKey;

    @Transactional
    @RabbitListener(queues = "${rabbitmq.queue.retry-name}")
    public void consumeRetryMessage(CouponIssueRequestMessage message) {
        log.info("retry Queue에서 받은 메세지: {}", message);

        // 원래 발급 로직 재시도
        try {
            User user = userRepository.findById(message.getUserId())
                    .orElseThrow(() -> new ClientException(ErrorCode.USER_NOT_FOUND));

            AuthUser authUser = AuthUser.of(user.getId(), user.getUserRole());
            patientCouponService.issue(authUser, message.getCouponId());
        } catch (Exception e) {
            log.warn("retry queue 처리 실패, fail queue로 이동 시도: {}", e.getMessage());
            try {
                rabbitTemplate.convertAndSend(exchange, failRoutingKey, message);
                log.info("메세지 fail 큐로 전송 완료: {}", message);
            } catch (Exception sendFailEx) {
                log.error("fail 큐로 전송 실패", sendFailEx);
            }
        }
    }
}
