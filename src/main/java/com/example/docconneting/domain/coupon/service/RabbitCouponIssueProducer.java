package com.example.docconneting.domain.coupon.service;

import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.coupon.dto.request.CouponIssueRequestMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitCouponIssueProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public void sendCouponIssueMessage(AuthUser authUser, Long couponId) {
        CouponIssueRequestMessage message = CouponIssueRequestMessage.of(authUser.getId(), couponId);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}

