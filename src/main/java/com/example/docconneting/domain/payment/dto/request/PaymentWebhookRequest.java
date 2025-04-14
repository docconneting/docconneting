package com.example.docconneting.domain.payment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentWebhookRequest {

    private String impUid;
    private String merchantUId;
    private String paymentStatus;
    private String payMethod;
}
