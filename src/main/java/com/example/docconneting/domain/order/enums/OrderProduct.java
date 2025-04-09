package com.example.docconneting.domain.order.enums;

import com.example.docconneting.common.exception.constant.ErrorCode;
import com.example.docconneting.common.exception.object.ClientException;

public enum OrderProduct {
    POINT_1000(1000),
    POINT_5000(5000),
    POINT_10000(10000),
    CHAT_3000(3000); // 채팅용 상품

    private final Integer price;

    OrderProduct(Integer price) {
        this.price = price;
    }

    public Integer getPrice() {
        return price;
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
