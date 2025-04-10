package com.example.docconneting.domain.order.dto.request;

import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequest {
    private OrderType orderType;
    private OrderProduct orderProduct;
    private Integer price;
}