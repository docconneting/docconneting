package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.domain.coupon.dto.request.CouponIssueRequestMessage;
import com.example.docconneting.domain.coupon.entity.FailedMessage;
import com.example.docconneting.domain.coupon.repository.FailedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FailConsumer {

    private final FailedMessageRepository failedMessageRepository;

    @Transactional
    @RabbitListener(queues = "${coupon.queue.fail-name}")
    public void consumeFailMessage(CouponIssueRequestMessage message) {
        log.error("DLQ에서 받은 메세지: {}", message);

        FailedMessage failed = FailedMessage.of(message, "Fail Queue 수신: 쿠폰 발급 실패");
        failedMessageRepository.save(failed);
    }
}
