package com.example.docconneting.domain.payment.dto.request;

import com.example.docconneting.domain.order.dto.request.OrderRequest;
import lombok.Getter;

@Getter
public class PaymentVerificationRequest {
    private String impUid;
    private Long userId;
    private String merchantId;
    private OrderRequest orderRequest;
}
