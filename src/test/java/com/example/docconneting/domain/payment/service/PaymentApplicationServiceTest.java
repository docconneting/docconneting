package com.example.docconneting.domain.payment.service;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;
import com.example.docconneting.domain.order.dto.request.OrderRequest;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.entity.Order;
import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.order.repository.OrderRepository;
import com.example.docconneting.domain.order.service.OrderService;
import com.example.docconneting.domain.payment.dto.request.PaymentVerificationRequest;
import com.example.docconneting.domain.payment.dto.request.PaymentWebhookRequest;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.user.entity.User;
import com.example.docconneting.domain.user.enums.UserRole;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.example.docconneting.common.exception.constant.ErrorCode.INVALID_PAYMENT_AMOUNT;
import static com.example.docconneting.common.exception.constant.ErrorCode.PAYMENT_SERVER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;


@SpringBootTest
@Transactional
class PaymentApplicationServiceTest {

    @Autowired
    private PaymentApplicationService paymentApplicationService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private PortOneService portOneService;

    private User user;
    private OrderRequest orderRequest;
    private Order order;
    private PaymentVerificationRequest paymentVerificationRequest;


    @BeforeEach
    void setUp() {
        // 유저 생성
        user = User.of("test@test.com", "test123!", "환자", 0, false, UserRole.PATIENT);
        ReflectionTestUtils.setField(user, "id", 1L);

        // 주문 요청 생성
        orderRequest = new OrderRequest();
        ReflectionTestUtils.setField(orderRequest, "orderType", OrderType.POINT);
        ReflectionTestUtils.setField(orderRequest, "orderProduct", OrderProduct.POINT_5000);
        ReflectionTestUtils.setField(orderRequest, "price", 5000);

        // 결제 검증 요청 생성
        paymentVerificationRequest = new PaymentVerificationRequest();
        ReflectionTestUtils.setField(paymentVerificationRequest, "impUid", "imp_123456");
        ReflectionTestUtils.setField(paymentVerificationRequest, "userId", 1L);
        ReflectionTestUtils.setField(paymentVerificationRequest, "merchantId", "merchant_abc123");
        ReflectionTestUtils.setField(paymentVerificationRequest, "orderRequest", orderRequest);
    }

    @Test
    @DisplayName("PG 결제 검증 및 주문 생성 성공")
    void PG_결제_검증_및_주문_생성_성공() throws Exception {
        LocalDateTime approvedAt = LocalDateTime.now();
        Payment iamportPayment = mock(Payment.class);
        when(iamportPayment.getAmount()).thenReturn(BigDecimal.valueOf(5000));
        when(iamportPayment.getStatus()).thenReturn("paid");
        when(iamportPayment.getPgProvider()).thenReturn("KAKAOPAY");
        when(iamportPayment.getPayMethod()).thenReturn("card");
        when(iamportPayment.getPaidAt()).thenReturn(Date.from(approvedAt.atZone(ZoneId.systemDefault()).toInstant()));

        when(portOneService.getPayment("imp_123456")).thenReturn(iamportPayment);

        OrderResponse response = paymentApplicationService.verifyAndCreateOrder(paymentVerificationRequest);

        Order savedOrder = orderRepository.findById(response.getId()).orElseThrow();

        assertThat(response).isNotNull();
        assertThat(savedOrder.getOrderProduct()).isEqualTo(OrderProduct.POINT_5000);
        assertThat(savedOrder.getPrice()).isEqualTo(5000);
        assertThat(savedOrder.getPaymentMethod()).isEqualTo(PaymentMethod.KAKAOPAY);
        assertThat(savedOrder.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("IamportResponseException 발생 테스트")
    void iamportResponseException_발생() throws Exception {
        // 가짜 HTTP Response (500 에러)
        Response<?> fakeResponse = Response.error(500, okhttp3.ResponseBody.create(null, "Server Error"));
        HttpException httpException = new HttpException(fakeResponse);

        when(portOneService.getPayment("imp_123456"))
                .thenThrow(new IamportResponseException("포트원 서버 오류", httpException));

        Throwable thrown = catchThrowable(() -> {
            paymentApplicationService.verifyAndCreateOrder(paymentVerificationRequest);
        });

        assertThat(thrown)
                .isInstanceOf(ClientException.class)
                .extracting("errorCode")
                .isEqualTo(PAYMENT_SERVER_ERROR);
    }

    @Test
    @DisplayName("PG 통신 중 IOException 발생 시 예외 반환")
    void ioException_발생() throws Exception {
        when(portOneService.getPayment("imp_123456"))
                .thenThrow(new IOException("네트워크 오류"));

        assertThatThrownBy(() -> paymentApplicationService.verifyAndCreateOrder(paymentVerificationRequest))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("네트워크 오류");
    }

    @Test
    @DisplayName("결제 상태가 COMPLETED면 결제 완료 처리")
    void 결제_상태가_COMPLETED면_결제_완료_처리() throws Exception {
        LocalDateTime approvedAt = LocalDateTime.now();

        // 1. 주문 생성 및 저장
        order = Order.ofPointOrder(user, OrderProduct.POINT_5000, "merchant_abc");
        order = orderRepository.save(order); // 실제 저장
        ReflectionTestUtils.setField(order, "paymentMethod", PaymentMethod.KAKAOPAY);
        ReflectionTestUtils.setField(order, "paymentStatus", PaymentStatus.FAILED);
        ReflectionTestUtils.setField(order, "orderStatus", OrderStatus.EXPIRED);
        orderRepository.save(order); // 변경사항 저장

        // 2. 포트원 결제 응답 Mock 설정
        Payment iamportPayment = mock(Payment.class);
        when(iamportPayment.getPgProvider()).thenReturn("KAKAOPAY");
        when(iamportPayment.getPayMethod()).thenReturn("card");
        when(iamportPayment.getPaidAt()).thenReturn(Date.from(approvedAt.atZone(ZoneId.systemDefault()).toInstant()));
        when(iamportPayment.getImpUid()).thenReturn("imp_123456");

        when(portOneService.getPayment("imp_123456")).thenReturn(iamportPayment);

        PaymentWebhookRequest request = PaymentWebhookRequest.of("imp_123456", "merchant_abc", "paid", "card");
        paymentApplicationService.handleWebhook(request);

        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(updatedOrder.getImpUid()).isEqualTo("imp_123456");
        assertThat(updatedOrder.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("결제 금액 불일치 시 예외 발생")
    void 결제_금액_불일치_예외() throws Exception {
        Payment iamportPayment = mock(Payment.class);
        when(iamportPayment.getAmount()).thenReturn(BigDecimal.valueOf(9999)); // 다르게 설정
        when(iamportPayment.getStatus()).thenReturn("paid");

        when(portOneService.getPayment("imp_123456")).thenReturn(iamportPayment);

        Throwable thrown = catchThrowable(() -> {
            paymentApplicationService.verifyAndCreateOrder(paymentVerificationRequest);
        });

        assertThat(thrown)
                .isInstanceOf(ClientException.class)
                .extracting("errorCode")
                .isEqualTo(INVALID_PAYMENT_AMOUNT);
    }

    @Test
    @DisplayName("Webhook 요청 시 주문이 존재하지 않으면 예외 발생")
    void webhook_주문_없을때_예외() throws Exception {
        // 실제로 주문을 저장하지 않음 (DB에 없음)
        PaymentWebhookRequest request = PaymentWebhookRequest.of("imp_xxx", "merchant_not_exist", "paid", "card");

        assertThatThrownBy(() -> paymentApplicationService.handleWebhook(request))
                .isInstanceOf(ClientException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);
    }
}