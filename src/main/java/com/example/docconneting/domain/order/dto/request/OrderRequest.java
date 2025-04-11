package com.example.docconneting.domain.order.dto.request;

import com.example.docconneting.domain.order.enums.OrderProduct;
import com.example.docconneting.domain.order.enums.OrderType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequest {

    @NotNull(message = "주문 타입은 필수입니다.")
    private OrderType orderType;

    @NotNull(message = "주문 상품은 필수입니다.")
    private OrderProduct orderProduct;

    @NotNull(message = "결제 금액은 필수입니다.")
    private Integer price;
}