package com.example.docconneting.domain.payment.controller;

import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.payment.dto.request.PaymentVerificationRequest;
import com.example.docconneting.domain.payment.dto.request.PaymentWebhookRequest;
import com.example.docconneting.domain.payment.dto.response.PortOnePaymentResponse;
import com.example.docconneting.domain.payment.service.PaymentApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")

public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    // 포트원 PG 사의 웹훅 콜백 처리
    @PostMapping("/webhook")
    public ResponseEntity<PortOnePaymentResponse> handleWebhook(
            @RequestBody PaymentWebhookRequest request
    ) throws Exception {
        return ResponseEntity.ok(paymentApplicationService.handleWebhook(request));
        }

    @PostMapping("/verify")
    public ResponseEntity<OrderResponse> verifyAndCreateOrder(
            @RequestBody PaymentVerificationRequest request
    ) throws Exception {
        return ResponseEntity.ok(paymentApplicationService.verifyAndCreateOrder(request));
    }
}
