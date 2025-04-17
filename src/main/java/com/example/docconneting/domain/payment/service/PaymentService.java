package com.example.docconneting.domain.payment.service;

import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.dto.response.ChattingRoomCreateResponse;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.payment.entity.PaymentHistory;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ChattingRoomService chattingRoomService;

    @Transactional
    // 결제 성공
    public void completePayment(Order order, String impUid, PaymentMethod paymentMethod, LocalDateTime approvedAt) {
        // 주문 상태 업데이트
        order.completeOrder(impUid, paymentMethod, approvedAt);
        orderRepository.save(order);

        // 결제 히스토리 기록
        paymentHistoryRepository.save(PaymentHistory.of(
                order.getUser(), order, order.getPrice(), paymentMethod, PaymentStatus.COMPLETED, impUid, approvedAt
        ));

        // 채팅 주문이면 채팅방 생성
        if (OrderType.CHAT.equals(order.getOrderType())) {
            AuthUser authUser = AuthUser.of(order.getUser().getId(), order.getUser().getUserRole());
            ChattingRoomCreateResponse response = chattingRoomService.createdChattingRoom(authUser, order.getDoctorId());
            order.assignChattingRoomId(response.getId());

            log.info("💬 ChattingRoom assigned: chattingRoomId={}, orderId={}", response.getId(), order.getId());
        }
    }

    @Transactional
    // 결제 실패
    public void failPayment(Order order) {
        order.failOrder();
    }
}