package com.example.docconneting.domain.payment.controller;

import com.example.docconneting.domain.payment.dto.request.PaymentWebhookRequest;
import com.example.docconneting.domain.payment.dto.response.PortOnePaymentResponse;
import com.example.docconneting.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    // 포트원 PG 사의 웹훅 콜백 처리
    @PostMapping("/webhook")
    public ResponseEntity<PortOnePaymentResponse> handleWebhook(
            @RequestBody PaymentWebhookRequest paymentWebhookRequest
    ) {
        PortOnePaymentResponse portOnePaymentResponse = paymentService.handleWebhook(paymentWebhookRequest);
        return ResponseEntity.ok(portOnePaymentResponse);
    }
}
