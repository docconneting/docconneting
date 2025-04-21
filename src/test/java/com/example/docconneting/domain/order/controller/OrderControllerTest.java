package com.example.docconneting.domain.order.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.filter.JwtFilter;
import com.example.docconneting.common.resolver.AuthUserArgumentResolver;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.order.service.OrderService;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import com.example.docconneting.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "jwt.secret.key=5Gk6hibHDtKLFVk4NdBX039rvehSLNjfKsdXpm/pHsU="
})
@Import({JwtUtil.class, JwtFilter.class, AuthUserArgumentResolver.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    AuthUserArgumentResolver authUserArgumentResolver;

    @MockitoBean
    private OrderService orderService;

    private AuthUser authUser;

    @Test
    @DisplayName("주문 단건 조회")
    void 주문_단건_조회() throws Exception {

        Long userId = 1L;
        UserRole role = UserRole.PATIENT;
        Long orderId = 1L;
        OrderType orderType = OrderType.POINT;
        OrderStatus orderStatus = OrderStatus.COMPLETED;
        PaymentStatus paymentStatus = PaymentStatus.COMPLETED;
        PaymentMethod paymentMethod = PaymentMethod.KAKAOPAY;
        OrderProduct orderProduct = OrderProduct.POINT_5000;
        Integer price = 5000;
        Long chattingRoomId = 30L;
        LocalDateTime approvedAt = LocalDateTime.now();

        AuthUser authUser = AuthUser.of(userId, role);
        String accessToken = jwtUtil.createToken(userId, role);

        OrderResponse orderResponse = OrderResponse.of(orderId, orderType, orderStatus, paymentStatus, paymentMethod, orderProduct, price, chattingRoomId, approvedAt);

        given(orderService.findOrderById(refEq(authUser), eq(orderId))).willReturn(orderResponse);

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                .header("Authorization",accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.orderType").value("POINT"))
                .andExpect(jsonPath("$.data.orderStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.data.paymentMethod").value("KAKAOPAY"))
                .andExpect(jsonPath("$.data.price").value(5000));
    }

    @Test
    @DisplayName("주문 목록 조회")
    void 주문_목록_조회() throws Exception {
        Long userId = 1L;
        UserRole userRole = UserRole.PATIENT;
        AuthUser authUser = AuthUser.of(userId, userRole);
        String accessToken = jwtUtil.createToken(userId, userRole);

        Long orderId = 1L;
        OrderType orderType = OrderType.POINT;
        OrderStatus orderStatus = OrderStatus.COMPLETED;
        PaymentStatus paymentStatus = PaymentStatus.COMPLETED;
        PaymentMethod paymentMethod = PaymentMethod.KAKAOPAY;
        OrderProduct orderProduct = OrderProduct.POINT_5000;
        Integer price = 5000;
        Long chattingRoomId = 30L;
        LocalDateTime approvedAt = LocalDateTime.now();

        OrderResponse orderResponse = OrderResponse.of(
                orderId, orderType, orderStatus, paymentStatus, paymentMethod, orderProduct, price, chattingRoomId, approvedAt
        );

        PageInfo pageInfo = new PageInfo(0, 10, 1,1);
        PageResult<OrderResponse> pageResult = new PageResult<>(
                List.of(orderResponse), pageInfo);

        given(orderService.findOrders(refEq(authUser), any(Pageable.class))).willReturn(pageResult);

        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .header("Authorization",accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].orderProduct").value("POINT_5000"))
                .andExpect(jsonPath("$.page.totalElement").value(1))
                .andExpect(jsonPath("$.page.totalPage").value(1));
    }
}