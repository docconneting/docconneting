package com.example.docconneting.domain.payment.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.payment.dto.request.PaymentWebhookRequest;
import com.example.docconneting.domain.payment.dto.response.PortOnePaymentResponse;
import com.example.docconneting.domain.payment.entity.PaymentHistory;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.payment.repository.PaymentHistoryRepository;
import com.example.docconneting.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ChattingRoomService chattingRoomService;

    @Transactional
    public PortOnePaymentResponse handleWebhook (PaymentWebhookRequest request) {
        // 상태 파싱
        PaymentStatus paymentStatus = PaymentStatus.of(request.getPaymentStatus());

        // 주문 조회
        Order order = orderRepository.findByMerchantUid(request.getMerchantUid())
                .orElseThrow(() -> new ClientException(ErrorCode.ORDER_NOT_FOUND));

        // 결제 완료인 경우 이력 저장 & 주문 정보 업데이트
        if (paymentStatus == PaymentStatus.COMPLETED) {
            PaymentMethod paymentMethod = PaymentMethod.of(request.getPayMethod());

            savePaymentHistory(
                    order,
                    order.getUser(),
                    request.getImpUid(),
                    request.getMerchantUid(),
                    paymentMethod,
                    LocalDateTime.now()
            );

            if (order.getOrderType() == OrderType.CHAT) {
                User user = order.getUser();
                AuthUser authUser = AuthUser.of(user.getId(), user.getUserRole());
                ChattingRoomCreateResponse response = chattingRoomService.createdChattingRoom(authUser, order.getDoctorId());
                order.assignChattingRoomId(response.getId());
            }
        }
        // 결제 실패 처리
        else if (paymentStatus == PaymentStatus.FAILED) {
            order.updatePaymentStatus(PaymentStatus.FAILED);
            order.updateOrderStatus(OrderStatus.EXPIRED);
        }
        // 응답 반환
        return PortOnePaymentResponse.of(
                order.getImpUid(),
                order.getMerchantUid(),
                order.getPrice(),
                order.getOrderStatus(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getCreatedAt()
        );
    }

    @Transactional
    public void savePaymentHistory(Order order, User user, String impUid, String merchantUid, PaymentMethod paymentMethod, LocalDateTime createdAt) {

        // 결제 이력 저장
        PaymentHistory history = PaymentHistory.of(
                user,
                order,
                order.getPrice(),
                paymentMethod,
                PaymentStatus.COMPLETED,
                impUid,
                createdAt
        );
        paymentHistoryRepository.save(history);

        // 주문 요약 정보 업데이트
        order.updatePaymentSummary(impUid, merchantUid, paymentMethod, createdAt);
        order.updatePaymentStatus(PaymentStatus.COMPLETED);
        order.updateOrderStatus(OrderStatus.COMPLETED);
    }
}