package com.example.docconneting.domain.payment.controller;

import com.example.docconneting.domain.payment.dto.request.PaymentWebhookRequest;
import com.example.docconneting.domain.payment.dto.response.PortOnePaymentResponse;
import com.example.docconneting.domain.payment.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    private IamportClient iamportClient;

    @Value("${portone.api-key}")
    private String apiKey;

    @Value("${portone.api-secret}")
    private String apiSecret;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, apiSecret);
    }
    // 포트원 PG 사의 웹훅 콜백 처리
    @PostMapping("/webhook")
    public ResponseEntity<PortOnePaymentResponse> handleWebhook(
            @RequestBody PaymentWebhookRequest paymentWebhookRequest
    ) {
        PortOnePaymentResponse portOnePaymentResponse = paymentService.handleWebhook(paymentWebhookRequest);
        return ResponseEntity.ok(portOnePaymentResponse);
    }
}
