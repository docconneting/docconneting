package com.example.docconneting.domain.order.controller;

import com.example.docconneting.common.response.PageResult;
import com.example.docconneting.common.response.Response;
import com.example.docconneting.domain.auth.annotation.Auth;
import com.example.docconneting.domain.auth.entity.AuthUser;
import com.example.docconneting.domain.order.dto.response.OrderResponse;
import com.example.docconneting.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Response<OrderResponse>> findOrderById(
            @Auth AuthUser authUser,
            @PathVariable Long orderId
    ) {
        OrderResponse orderResponse = orderService.findOrderById(authUser, orderId);
        return ResponseEntity.ok(Response.of(orderResponse));
    }

    @GetMapping
    public ResponseEntity<Response<List<OrderResponse>>> findAllOrders(
            @Auth AuthUser authUser,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResult<OrderResponse> orders = orderService.findOrders(authUser, pageable);

        return ResponseEntity.ok(Response.of(orders.getContent(), orders.getPageInfo()));
    }
}
