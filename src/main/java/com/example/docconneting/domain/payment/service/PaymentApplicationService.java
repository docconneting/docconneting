package com.example.docconneting.domain.payment.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.order.service.OrderService;
import com.example.docconneting.domain.payment.dto.request.PaymentVerificationRequest;
import com.example.docconneting.domain.payment.dto.request.PaymentWebhookRequest;
import com.example.docconneting.domain.payment.dto.response.PortOnePaymentResponse;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.user.enums.UserRole;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentApplicationService {

    private final PaymentService paymentService;
    private final PortOneService portOneService;
    private final OrderService orderService;
    private final ChattingRoomService chattingRoomService;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse verifyAndCreateOrder(PaymentVerificationRequest request) {
        try {
            // PG 결제 검증
            Payment payment = portOneService.getPayment(request.getImpUid());
            int paidAmount = payment.getAmount().intValue();
            int expectedAmount = request.getOrderRequest().getPrice();

            if (paidAmount != expectedAmount) {
                throw new ClientException(ErrorCode.INVALID_PAYMENT_AMOUNT);
            }

            if (!"paid".equals(payment.getStatus())) {
                throw new ClientException(ErrorCode.PAYMENT_NOT_COMPLETED);
            }

            // 주문 생성
            AuthUser authUser = AuthUser.of(request.getUserId(), UserRole.PATIENT);
            Order order = orderService.createOrder(request.getOrderRequest(), request.getMerchantId(), authUser);

            // 결제 완료 처리
            PaymentMethod paymentMethod = PaymentMethod.of(payment.getPgProvider(), payment.getPayMethod());
            LocalDateTime approvedAt = payment.getPaidAt().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            paymentService.completePayment(order, payment.getImpUid(), paymentMethod, approvedAt);

            // 채팅 주문 후처리
            if (order.isChatOrder()) {
                ChattingRoomCreateResponse response = chattingRoomService.createdChattingRoom(authUser, order.getDoctorId());
                order.assignChattingRoomId(response.getId());
            }

            return OrderResponse.of(
                    order.getId(),
                    order.getOrderType(),
                    order.getOrderStatus(),
                    order.getPaymentStatus(),
                    order.getPaymentMethod(),
                    order.getOrderProduct(),
                    order.getPrice(),
                    order.getChattingRoomId(),
                    order.getApprovedAt()
            );

        } catch (IamportResponseException | IOException e) {
            throw new ClientException(ErrorCode.PAYMENT_SERVER_ERROR);
        }
    }

    @Transactional
    public PortOnePaymentResponse handleWebhook(PaymentWebhookRequest request) throws IamportResponseException, IOException {
        Order order = orderRepository.findByMerchantUid(request.getMerchantUid())
                .orElseThrow(() -> new ClientException(ErrorCode.ORDER_NOT_FOUND));
        Payment payment = portOneService.getPayment(request.getImpUid());

        PaymentMethod paymentMethod = PaymentMethod.of(payment.getPgProvider(), payment.getPayMethod());
        LocalDateTime approvedAt = payment.getPaidAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();

        if (PaymentStatus.COMPLETED.equals(PaymentStatus.of(request.getPaymentStatus()))) {
            if (!order.isCompleted()) {
                paymentService.completePayment(order, payment.getImpUid(), paymentMethod, approvedAt);
            } else {
                log.info("이미 완료된 주문입니다: {}", order.getId());
            }
        } else {
            paymentService.failPayment(order);
        }

        return PortOnePaymentResponse.of(
                order.getImpUid(),
                order.getMerchantUid(),
                order.getPrice(),
                order.getOrderStatus(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getApprovedAt()
        );
    }
}

