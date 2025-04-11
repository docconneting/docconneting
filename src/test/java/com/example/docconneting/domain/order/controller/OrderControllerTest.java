package com.example.docconneting.domain.order.controller;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.common.response.PageInfo;
import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.domain.order.dto.request.OrderRequest;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.order.service.OrderService;
import com.example.docconneting.domain.user.enums.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private OrderService orderService;

    @Test
    void 주문_성공() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = jwtUtil.createToken(userId, UserRole.PATIENT);

        OrderRequest request = new OrderRequest();
        ReflectionTestUtils.setField(request, "orderType", OrderType.POINT);
        ReflectionTestUtils.setField(request, "orderProduct", OrderProduct.POINT_5000);
        ReflectionTestUtils.setField(request, "price", 5000);

        OrderResponse response = OrderResponse.of(
                1L,
                OrderType.POINT,
                OrderStatus.REQUESTED,
                OrderProduct.POINT_5000,
                5000,
                null,
                LocalDateTime.now()
        );

        given(orderService.createOrder(any(), refEq(request)))
                .willReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderType").value("POINT"))
                .andExpect(jsonPath("$.data.orderProduct").value("POINT_5000"))
                .andExpect(jsonPath("$.data.price").value(5000));

    }

    @Test
    void 주문_단건_조회() throws Exception {
        // given
        Long userId = 1L;
        Long orderId = 2L;
        String accessToken = jwtUtil.createToken(userId, UserRole.PATIENT);

        OrderResponse response = OrderResponse.of(
                orderId,
                OrderType.POINT,
                OrderStatus.REQUESTED,
                OrderProduct.POINT_5000,
                5000,
                null,
                LocalDateTime.now()
        );

        given(orderService.findOrderById(any(), eq(orderId)))
                .willReturn(response);


        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                        .header("Authorization",accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderType").value("POINT"))
                .andExpect(jsonPath("$.data.orderProduct").value("POINT_5000"))
                .andExpect(jsonPath("$.data.price").value(5000));
    }

    @Test
    void 주문_목록_조회() throws Exception {
        // given
        Long userId = 1L;
        String accessToken = jwtUtil.createToken(userId, UserRole.PATIENT);

        List<OrderResponse> content = List.of(
                OrderResponse.of(1L, OrderType.POINT, OrderStatus.REQUESTED, OrderProduct.POINT_5000, 5000, null, LocalDateTime.now()),
                OrderResponse.of(2L, OrderType.CHAT, OrderStatus.REQUESTED, OrderProduct.CHAT_3000, 3000, null, LocalDateTime.now())
        );

        PageInfo pageInfo = PageInfo.builder()
                .pageNum(0)
                .pageSize(10)
                .totalElement(2L)
                .totalPage(1)
                .build();

        PageResult<OrderResponse> pageResult = new PageResult<>(content, pageInfo);
        Pageable expectedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        given(orderService.findOrders(any(), refEq(expectedPageable))).willReturn(pageResult);

        mockMvc.perform(get("/api/v1/orders")
                        .header("Authorization", accessToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].orderType").value("POINT"))
                .andExpect(jsonPath("$.data[1].orderProduct").value("CHAT_3000"))
                .andExpect(jsonPath("$.page.totalElement").value(2));
    }
}