package com.example.docconneting.domain.payment.service;

import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.chatting.service.ChattingRoomService;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.payment.entity.PaymentHistory;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.repository.PaymentHistoryRepository;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    ChattingRoomService chattingRoomService;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private AuthUser authUser;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.of("test@test.com", "test123!", "환자", 0, false, UserRole.PATIENT);
        ReflectionTestUtils.setField(user, "id", 1L);

        order = Order.ofPointOrder(user, OrderProduct.POINT_5000, "merchant_uid");
        ReflectionTestUtils.setField(order, "id", 10L);
    }

    @Test
    @DisplayName("결제 성공")
    void 결제_성공() {
        String impUid = "imp_123456";
        PaymentMethod method = PaymentMethod.KAKAOPAY;
        LocalDateTime approvedAt = LocalDateTime.now();

        paymentService.completePayment(order, impUid, method, approvedAt);

        verify(orderRepository).save(order);
        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
    }

    @Test
    @DisplayName("결제 실패")
    void 결제_실패() {
        paymentService.failPayment(order);

        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.EXPIRED);
    }
}