package com.example.docconneting.domain.payment.dto.response;

import com.example.docconneting.domain.order.enums.OrderStatus;
import com.example.docconneting.domain.payment.enums.PaymentMethod;
import com.example.docconneting.domain.payment.enums.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PortOnePaymentResponse {

    private String impUid;
    private String merchantUId;
    private Integer price;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;

    private PortOnePaymentResponse(String impUid, String merchantUId, Integer price, OrderStatus orderStatus, PaymentStatus paymentStatus, PaymentMethod paymentMethod, LocalDateTime createdAt) {
        this.impUid = impUid;
        this.merchantUId = merchantUId;
        this.price = price;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    public static PortOnePaymentResponse of(String impUid, String merchantUId, Integer price, OrderStatus orderStatus, PaymentStatus paymentStatus, PaymentMethod paymentMethod, LocalDateTime createdAt) {
        return new PortOnePaymentResponse(impUid, merchantUId, price, orderStatus, paymentStatus, paymentMethod, createdAt);
    }
}
