package com.example.docconneting.domain.payment.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.filter.JwtFilter;
import com.example.docconneting.common.resolver.AuthUserArgumentResolver;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.payment.dto.request.PaymentVerificationRequest;
import com.example.docconneting.domain.payment.dto.request.PaymentWebhookRequest;
import com.example.docconneting.domain.payment.dto.response.PortOnePaymentResponse;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.payment.service.PaymentApplicationService;
import com.example.docconneting.domain.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "portone.api.key=3340373828478256",
        "portone.api.secret=IEAKRBNbQNsiKJI9mr32aSZSWgTwDlWHkvcdoFQErGbwRRQbVJXF7GvU7GV4toqZdQrUT4hQdVPz6QJf",
        "jwt.secret.key=5Gk6hibHDtKLFVk4NdBX039rvehSLNjfKsdXpm/pHsU="
})
@Import({JwtUtil.class, JwtFilter.class, AuthUserArgumentResolver.class})
class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtUtil jwtUtil;

    @MockitoBean private PaymentApplicationService paymentApplicationService;

    @Test
    @DisplayName("PG 결제 검증 및 주문 생성 성공")
    void verifyAndCreateOrder() throws Exception {
        PaymentVerificationRequest request = new PaymentVerificationRequest();
        ReflectionTestUtils.setField(request, "impUid", "imp_123456");
        ReflectionTestUtils.setField(request, "merchantId", "merchant_abc");
        ReflectionTestUtils.setField(request, "userId", 1L);

        OrderResponse response = OrderResponse.of(
                1L, OrderType.POINT, OrderStatus.COMPLETED,
                PaymentStatus.COMPLETED, PaymentMethod.KAKAOPAY,
                OrderProduct.POINT_5000, 5000, null, LocalDateTime.now()
        );

        given(paymentApplicationService.verifyAndCreateOrder(argThat(req ->
                        req.getImpUid().equals("imp_123456") &&
                                req.getMerchantId().equals("merchant_abc") &&
                                req.getUserId().equals(1L)
                )
        )).willReturn(response);

        String accessToken = jwtUtil.createToken(1L, UserRole.PATIENT);

        mockMvc.perform(post("/api/v1/payments/verify")
                        .header("Authorization", accessToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("포트원 웹훅 결제 성공 처리")
    void handleWebhook() throws Exception {
        PaymentWebhookRequest request = PaymentWebhookRequest.of(
                "imp_123456", "merchant_abc", "paid", "card");

        PortOnePaymentResponse response = PortOnePaymentResponse.of(
                "imp_123456", "merchant_abc", 5000,
                OrderStatus.COMPLETED, PaymentStatus.COMPLETED,
                PaymentMethod.KAKAOPAY, LocalDateTime.now()
        );

        given(paymentApplicationService.handleWebhook(
                argThat(req ->
                        req.getImpUid().equals("imp_123456") &&
                                req.getMerchantUid().equals("merchant_abc") &&
                                req.getPaymentStatus().equals("paid") &&
                                req.getPayMethod().equals("card")
                )
        )).willReturn(response);


        mockMvc.perform(post("/api/v1/payments/webhook")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.impUid").value("imp_123456"))
                .andExpect(jsonPath("$.data.merchantUid").value("merchant_abc"))
                .andExpect(jsonPath("$.data.paymentStatus").value("COMPLETED"));
    }
}
