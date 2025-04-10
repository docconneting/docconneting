package com.example.docconneting.domain.order.dto.response;

import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.order.enums.OrderType;
import com.example.docconneting.domain.order.entity.Order;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponse {

    private Long id;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private OrderProduct orderProduct;
    private Integer price;
    private Long chattingUserId;
    private LocalDateTime approvedAt;

    private OrderResponse(Long id, OrderType orderType, OrderStatus orderStatus, OrderProduct orderProduct, Integer price, Long chattingUserId, LocalDateTime approvedAt) {
        this.id = id;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.orderProduct = orderProduct;
        this.price = price;
        this.chattingUserId = chattingUserId;
        this.approvedAt = approvedAt;
    }

    public static OrderResponse of(Long id, OrderType orderType, OrderStatus orderStatus, OrderProduct orderProduct, Integer price, Long chattingUserId, LocalDateTime approvedAt) {
        return new OrderResponse(id, orderType, orderStatus, orderProduct, price, chattingUserId, approvedAt);
    }

    public static List<OrderResponse> toOrderResponseList(List<Order> orders) {
        return orders.stream()
                .map(order -> OrderResponse.of(
                        order.getId(),
                        order.getOrderType(),
                        order.getOrderStatus(),
                        order.getOrderProduct(),
                        order.getPrice(),
                        order.getChattingRoomId(),
                        order.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
