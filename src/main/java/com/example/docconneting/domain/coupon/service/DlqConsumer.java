package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.domain.coupon.dto.request.CouponIssueRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DlqConsumer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${coupon.exchange.name}")
    private String exchange;
    @Value("${coupon.routing.retry-key}")
    private String retryRoutingKey;

    @Transactional
    @RabbitListener(queues = "${coupon.queue.dlq-name}")
    public void consumeDlqMessage(CouponIssueRequestMessage message) {
        log.error("DLQ에서 받은 메세지: {}", message);

        try {
            rabbitTemplate.convertAndSend(exchange, retryRoutingKey, message);
            log.info("메세지 retry 큐로 전송 완료: {}", message);
        } catch (Exception e) {
            log.error("retry 큐로 전송 실패", e);
        }
    }
}
