package com.example.docconneting.domain.order.enums;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;

public enum OrderProduct {
    POINT_1000(1000, OrderType.POINT),
    POINT_5000(5000, OrderType.POINT),
    POINT_10000(10000, OrderType.POINT),
    CHAT_3000(3000, OrderType.CHAT); // 채팅용 상품

    private final Integer price;
    private final OrderType orderType;

    OrderProduct(Integer price, OrderType orderType) {
        this.price = price;
        this.orderType = orderType;
    }

    public Integer getPrice() {
        return price;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public static OrderProduct of(Integer price) {
        for (OrderProduct orderProduct : values()) {
            if (orderProduct.price.equals(price)) {
                return orderProduct;
            }
        }
        throw new ClientException(ErrorCode.ORDER_PRODUCT_NOT_FOUND);
    }
}
