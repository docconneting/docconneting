package com.example.docconneting.domain.payment.controller;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.order.service.OrderService;
import com.example.docconneting.domain.payment.dto.request.PaymentVerificationRequest;
import com.example.docconneting.domain.payment.dto.request.PaymentWebhookRequest;
import com.example.docconneting.domain.payment.dto.response.PortOnePaymentResponse;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.payment.service.PaymentService;
import com.example.docconneting.domain.payment.service.PortOneService;
import com.example.docconneting.domain.user.enums.UserRole;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PortOneService portOneService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    private IamportClient iamportClient;

    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
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
        log.info("📡 Webhook 호출됨!");
        log.info("✅ merchant_uid={}, status={}, pay_method={}",
                paymentWebhookRequest.getMerchantUid(),
                paymentWebhookRequest.getPaymentStatus(),
                paymentWebhookRequest.getPayMethod());
        try {
            Order order = orderRepository.findByMerchantUid(paymentWebhookRequest.getMerchantUid())
                    .orElseThrow(() -> new ClientException(ErrorCode.ORDER_NOT_FOUND));

            Payment payment = portOneService.getPayment(paymentWebhookRequest.getImpUid());
            String pgProvider = payment.getPgProvider();
            String payMethod = payment.getPayMethod();

            PaymentMethod paymentMethod = PaymentMethod.of(pgProvider, payment.getPayMethod());

            LocalDateTime approvedAt = payment.getPaidAt().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            if (PaymentStatus.COMPLETED.equals(PaymentStatus.of(paymentWebhookRequest.getPaymentStatus()))) {
                if (!order.isCompleted()) {
                    paymentService.completePayment(order, payment.getImpUid(), paymentMethod, approvedAt);
                } else {
                    log.info("이미 완료된 주문입니다: {}", order.getId());
                }
            } else {
                paymentService.failPayment(order);
            }

            return ResponseEntity.ok(
                    PortOnePaymentResponse.of(
                            order.getImpUid(),
                            order.getMerchantUid(),
                            order.getPrice(),
                            order.getOrderStatus(),
                            order.getPaymentStatus(),
                            order.getPaymentMethod(),
                            order.getApprovedAt())
            );
        } catch (IamportResponseException | IOException e) {
            log.error("Webhook 처리 중 에러 발생", e);
            throw new ClientException(ErrorCode.PAYMENT_VERIFICATION_FAILED);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<OrderResponse> verifyAndCreateOrder(
            @RequestBody PaymentVerificationRequest request
    ) throws IamportResponseException, IOException {
        Payment payment = portOneService.getPayment(request.getImpUid());
        int paidAmount = payment.getAmount().intValue();

        int expectedAmount = request.getOrderRequest().getPrice();

        if (paidAmount != expectedAmount) {
            throw new ClientException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        if (!"paid".equals(payment.getStatus())) {
            throw new ClientException(ErrorCode.PAYMENT_NOT_COMPLETED);
        }

        AuthUser authUser = AuthUser.of(request.getUserId(), UserRole.PATIENT);
        Order order = orderService.createOrder(request.getOrderRequest(), request.getMerchantId(), authUser);
        PaymentMethod paymentMethod = PaymentMethod.of(payment.getPgProvider(), payment.getPayMethod());

        LocalDateTime approvedAt = payment.getPaidAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();

        paymentService.completePayment(order, payment.getImpUid(), paymentMethod, approvedAt);

        return ResponseEntity.ok(OrderResponse.of(
                order.getId(),
                order.getOrderType(),
                order.getOrderStatus(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getOrderProduct(),
                order.getPrice(),
                order.getChattingRoomId(),
                order.getApprovedAt()
        ));
    }
}
